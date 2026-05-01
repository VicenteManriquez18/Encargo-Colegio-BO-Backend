package com.proyecto.ColegioBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.ColegioBackend.model.Usuario;
import com.proyecto.ColegioBackend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String correo = credentials.get("correo");
        String password = credentials.get("password");
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        
        // Validamos si existe el usuario y si la contraseña coincide con la encriptada en la BD
        if (usuarioOpt.isEmpty() || !passwordEncoder.matches(password, usuarioOpt.get().getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
        
        // Si todo es correcto, obtenemos al usuario y generamos su token real
        Usuario usuario = usuarioOpt.get();
        String token = generarJwt(usuario.getCorreo(), usuario.getRol());
        
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> user) {
        try {
            System.out.println("Registrando usuario: " + user.get("correo"));
            
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setCorreo(user.get("correo"));
            // Encriptamos la contraseña antes de guardarla
            nuevoUsuario.setPassword(passwordEncoder.encode(user.get("password")));
            nuevoUsuario.setRol(user.getOrDefault("rol", "Alumno")); // Usamos el rol de Postman, o Alumno por defecto
            
            Usuario guardado = usuarioRepository.save(nuevoUsuario); // ¡Guardado directo!
            System.out.println("¡ÉXITO! Usuario guardado en BD con ID: " + guardado.getId());
            
            return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado con éxito"));
        } catch (DataIntegrityViolationException e) {
            System.err.println("Intento de registro fallido: Correo duplicado.");
            return ResponseEntity.badRequest().body(Map.of("error", "Este correo ya está registrado. Por favor, usa otro o inicia sesión."));
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO AL GUARDAR EN BD: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Error de BD: " + e.getMessage()));
        }
    }

    // Método para generar un JWT REAL y FIRMADO usando Java nativo
    private String generarJwt(String correo, String rol) {
        try {
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            long exp = (System.currentTimeMillis() / 1000) + 86400; // Expira en 24 horas
            String payload = "{\"sub\":\"" + correo + "\",\"rol\":\"" + rol + "\",\"exp\":" + exp + "}";
            
            String encodedHeader = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes("UTF-8"));
            String encodedPayload = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes("UTF-8"));
            String data = encodedHeader + "." + encodedPayload;
            
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            String secret = "MiClaveSecretaSuperSeguraYLargaParaEsteProyectoDeSpring123"; // En prod va en application.properties
            mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            
            String signature = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes("UTF-8")));
            return data + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el token", e);
        }
    }
}