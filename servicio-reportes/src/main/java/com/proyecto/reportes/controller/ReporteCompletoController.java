package com.proyecto.reportes.controller;

import com.proyecto.reportes.dto.ReporteCompletoDTO;
import com.proyecto.reportes.service.ReporteCompletoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/reportes/completo")
@Tag(name = "Reportes Completos", description = "Endpoints para la obtención de reportes consolidados (notas, asistencia y comportamiento)")
public class ReporteCompletoController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReporteCompletoController.class);

    @Autowired
    private ReporteCompletoService reporteCompletoService;

    @GetMapping("/alumno/{alumnoId}")
    @Operation(summary = "Obtener reporte consolidado de un alumno",
            description = "Realiza peticiones a los servicios de académico y asistencia vía WebClient, y los consolida junto con el comportamiento registrado localmente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte consolidado obtenido con éxito")
    })
    public ResponseEntity<ReporteCompletoDTO> obtenerReporteCompleto(
            @PathVariable Long alumnoId,
            @RequestParam(value = "comportamiento", required = false, defaultValue = "true") boolean incluirComportamiento) {
        log.info("GET: Generando reporte consolidado completo para el alumno {} (incluir comportamiento: {})", alumnoId, incluirComportamiento);
        ReporteCompletoDTO reporte = reporteCompletoService.obtenerReporteCompleto(alumnoId, incluirComportamiento);
        return ResponseEntity.ok(reporte);
    }
}
