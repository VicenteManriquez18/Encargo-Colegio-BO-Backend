package com.proyecto.ColegioBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import com.proyecto.ColegioBackend.model.Estudiante;
import com.proyecto.ColegioBackend.services.EstudianteService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matricula")
@CrossOrigin(origins = "http://localhost:5173") // Ajustar según seguridad
public class EstudianteController {

    @Autowired
    private EstudianteService matriculaService;

    private final WebClient webClient = WebClient.create();

    @GetMapping("/estudiantes")
    public List<Estudiante> listarEstudiantes() {
        return matriculaService.listarTodosEstudiantes();
    }

    @PostMapping("/registrar-completo")
    public ResponseEntity<?> registrarMatricula(@RequestBody Estudiante estudiante) {
        try {
            Estudiante nuevoEstudiante = matriculaService.registrarMatriculaCompleta(estudiante);
            return new ResponseEntity<>(nuevoEstudiante, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al matricular: " + e.getMessage());
        }
    }

    @GetMapping("/estudiantes/rut/{rut}")
    public ResponseEntity<Estudiante> buscarPorRut(@PathVariable String rut) {
        Estudiante estudiante = matriculaService.buscarEstudiantePorRut(rut);
        return estudiante != null ? ResponseEntity.ok(estudiante) : ResponseEntity.notFound().build();
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
}
