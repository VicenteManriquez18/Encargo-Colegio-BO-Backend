package com.colegio.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/usuarios")
    public ResponseEntity<?> fallbackUsuarios() {
        return ResponseEntity.status(503).body(Map.of("error", "El servicio de usuarios no responde (Gateway Fallback)"));
    }

    @GetMapping("/matricula")
    public ResponseEntity<?> fallbackMatricula() {
        return ResponseEntity.status(503).body(Map.of("error", "El servicio de matrícula no responde (Gateway Fallback)"));
    }

    @GetMapping("/asistencia")
    public ResponseEntity<?> fallbackAsistencia() {
        return ResponseEntity.status(503).body(Map.of("error", "El servicio de asistencia no responde (Gateway Fallback)"));
    }

    @GetMapping("/academico")
    public ResponseEntity<?> fallbackAcademico() {
        return ResponseEntity.status(503).body(Map.of("error", "El servicio académico no responde (Gateway Fallback)"));
    }
}
