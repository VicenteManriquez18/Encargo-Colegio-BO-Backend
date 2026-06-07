package com.proyecto.ColegioBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import com.proyecto.ColegioBackend.model.Asistencia;
import com.proyecto.ColegioBackend.repository.AsistenciaRepository;
import com.proyecto.ColegioBackend.services.AsistenciaService;
import java.util.Map;
import java.util.List;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/asistencia")
public class AsistenciaController {

    @Autowired
    private AsistenciaRepository asistenciaRepository;
    
    @Autowired
    private AsistenciaService asistenciaService;

    // Instanciamos WebClient para comunicación entre microservicios
    private final WebClient webClient = WebClient.create();

    // ==================== GET ====================
    
    @GetMapping
    public List<Asistencia> listar() {
        return asistenciaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> buscarPorId(@PathVariable Long id) {
        Asistencia asistencia = asistenciaService.buscarPorId(id);
        if (asistencia != null) {
            return ResponseEntity.ok(asistencia);
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== POST ====================
    
    @PostMapping
    public ResponseEntity<Asistencia> guardar(@RequestBody Asistencia asistencia) {
        return new ResponseEntity<>(asistenciaService.guardar(asistencia), HttpStatus.CREATED);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarAsistencia(@RequestBody Map<String, Object> request) {
        Object idObj = request.get("usuarioId");
        if (idObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "El campo usuarioId es requerido"));
        }

        Long usuarioId = Long.valueOf(idObj.toString());

        try {
            // Usamos 127.0.0.1 para evitar problemas de resolución de localhost
            String url = "http://127.0.0.1:8081/api/usuarios/" + usuarioId;
            
            ResponseEntity<Map> response = webClient.get()
                .uri(url)
                .retrieve()
                .toEntity(Map.class)
                .block();
            Map<String, Object> usuario = response.getBody();

            if (usuario == null || !usuario.containsKey("correo")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado en el sistema central"));
            }

            Asistencia nuevaAsistencia = new Asistencia();
            nuevaAsistencia.setUsuarioId(usuarioId);
            nuevaAsistencia.setNombreUsuario((String) usuario.get("correo"));
            nuevaAsistencia.setFecha(LocalDateTime.now());

            return ResponseEntity.ok(asistenciaRepository.save(nuevaAsistencia));
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "ID de usuario inexistente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al conectar con el servicio de usuarios: " + e.getMessage()));
        }
    }

    // ==================== GET ALUMNOS DESDE USUARIOS ====================

    @GetMapping("/alumnos")
    public ResponseEntity<?> obtenerAlumnos(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            String url = "http://127.0.0.1:8081/api/usuarios";
            WebClient.RequestHeadersSpec<?> request = webClient.get().uri(url);
            if (token != null) {
                request = request.header("Authorization", token);
            }
            ResponseEntity<List> response = request.retrieve().toEntity(List.class).block();
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al conectar con el servicio de usuarios: " + e.getMessage()));
        }
    }

    // ==================== DELETE ====================
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asistenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}