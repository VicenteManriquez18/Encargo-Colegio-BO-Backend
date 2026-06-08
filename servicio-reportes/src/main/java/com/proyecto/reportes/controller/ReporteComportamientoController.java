package com.proyecto.reportes.controller;

import com.proyecto.reportes.entity.ReporteComportamiento;
import com.proyecto.reportes.repository.ReporteComportamientoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/reportes/comportamiento")
@Tag(name = "Reportes de Comportamiento", description = "Endpoints para la gestión de comportamiento y conducta escolar")
public class ReporteComportamientoController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReporteComportamientoController.class);

    @Autowired
    private ReporteComportamientoRepository comportamientoRepository;

    @PostMapping
    @Operation(summary = "Registrar comportamiento de un alumno",
            description = "Guarda una evaluación conductual para un alumno específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comportamiento registrado correctamente")
    })
    public ResponseEntity<ReporteComportamiento> registrarComportamiento(@RequestBody ReporteComportamiento comportamiento) {
        log.info("POST: Registrando comportamiento para el alumno {}", comportamiento.getAlumnoId());
        ReporteComportamiento guardado = comportamientoRepository.save(comportamiento);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    @GetMapping("/alumno/{alumnoId}")
    @Operation(summary = "Obtener comportamientos de un alumno",
            description = "Retorna el historial de evaluaciones conductuales registradas de un alumno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados")
    })
    public ResponseEntity<List<ReporteComportamiento>> obtenerComportamientosAlumno(@PathVariable Long alumnoId) {
        log.info("GET: Historial de comportamiento del alumno {}", alumnoId);
        List<ReporteComportamiento> lista = comportamientoRepository.findByAlumnoId(alumnoId);
        return ResponseEntity.ok(lista);
    }
}
