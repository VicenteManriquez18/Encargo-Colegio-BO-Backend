package com.proyecto.ColegioBackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.ColegioBackend.model.Usuario;
import com.proyecto.ColegioBackend.services.UsuarioService;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/usuarios")

public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService; // Inyectamos la interfaz, no la implementación

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Usuario> listar(@org.springframework.web.bind.annotation.RequestParam(required = false) String rol) {
        if (rol != null && !rol.isEmpty()) {
            return usuarioService.listarPorRol(rol);
        }
        return usuarioService.listarTodos();
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Usuario usuario) {
        String password = usuario.getPassword();
        if (password == null || password.length() < 8) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "La contraseña debe tener al menos 8 caracteres."));
        }
        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
        if (!hasUppercase || !hasLowercase || !hasDigit || !hasSpecial) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", 
                "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial."));
        }

        usuario.setPassword(passwordEncoder.encode(password));
        return new ResponseEntity<>(usuarioService.guardar(usuario), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        Usuario existente = usuarioService.buscarPorId(id);
        if (existente != null) {
            usuario.setId(id); // Aseguramos que el ID sea el correcto
            // Si se proporciona nueva contraseña, la encriptamos
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            } else {
                // Si no se proporciona, mantenemos la existente
                usuario.setPassword(existente.getPassword());
            }
            return ResponseEntity.ok(usuarioService.guardar(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
