package com.colegio.apigateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
@Tag(name = "Fallback", description = "Endpoints de fallback cuando los servicios no responden")
public class FallbackController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FallbackController.class);

    @GetMapping("/usuarios")
    @Operation(summary = "Fallback - Servicio de Usuarios",
            description = "Se ejecuta cuando el servicio de usuarios no responde")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "503", description = "Servicio de usuarios no disponible")
    })
    public ResponseEntity<?> fallbackUsuarios() {
        log.warn("Fallback: Servicio usuarios no responde");
        return ResponseEntity.status(503).body(Map.of(
                "error", "El servicio de usuarios no responde (Gateway Fallback)",
                "service", "usuarios",
                "status_code", 503
        ));
    }

    @GetMapping("/matricula")
    @Operation(summary = "Fallback - Servicio de Matrícula",
            description = "Se ejecuta cuando el servicio de matrícula no responde")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "503", description = "Servicio de matrícula no disponible")
    })
    public ResponseEntity<?> fallbackMatricula() {
        log.warn("Fallback: Servicio matrícula no responde");
        return ResponseEntity.status(503).body(Map.of(
                "error", "El servicio de matrícula no responde (Gateway Fallback)",
                "service", "matricula",
                "status_code", 503
        ));
    }

    @GetMapping("/asistencia")
    @Operation(summary = "Fallback - Servicio de Asistencia",
            description = "Se ejecuta cuando el servicio de asistencia no responde")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "503", description = "Servicio de asistencia no disponible")
    })
    public ResponseEntity<?> fallbackAsistencia() {
        log.warn("Fallback: Servicio asistencia no responde");
        return ResponseEntity.status(503).body(Map.of(
                "error", "El servicio de asistencia no responde (Gateway Fallback)",
                "service", "asistencia",
                "status_code", 503
        ));
    }

    @GetMapping("/academico")
    @Operation(summary = "Fallback - Servicio Académico",
            description = "Se ejecuta cuando el servicio académico no responde")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "503", description = "Servicio académico no disponible")
    })
    public ResponseEntity<?> fallbackAcademico() {
        log.warn("Fallback: Servicio académico no responde");
        return ResponseEntity.status(503).body(Map.of(
                "error", "El servicio académico no responde (Gateway Fallback)",
                "service", "academico",
                "status_code", 503
        ));
    }

    @GetMapping("/reportes")
    @Operation(summary = "Fallback - Servicio de Reportes",
            description = "Se ejecuta cuando el servicio de reportes no responde")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "503", description = "Servicio de reportes no disponible")
    })
    public ResponseEntity<?> fallbackReportes() {
        log.warn("Fallback: Servicio reportes no responde");
        return ResponseEntity.status(503).body(Map.of(
                "error", "El servicio de reportes no responde (Gateway Fallback)",
                "service", "reportes",
                "status_code", 503
        ));
    }

    @GetMapping("/mensajeria")
    @Operation(summary = "Fallback - Servicio de Mensajería",
            description = "Se ejecuta cuando el servicio de mensajería no responde")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "503", description = "Servicio de mensajería no disponible")
    })
    public ResponseEntity<?> fallbackMensajeria() {
        log.warn("Fallback: Servicio mensajería no responde");
        return ResponseEntity.status(503).body(Map.of(
                "error", "El servicio de mensajería no responde (Gateway Fallback)",
                "service", "mensajeria",
                "status_code", 503
        ));
    }
}
