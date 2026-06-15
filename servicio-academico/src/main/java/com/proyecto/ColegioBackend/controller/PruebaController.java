package com.proyecto.ColegioBackend.controller;

import com.proyecto.ColegioBackend.model.Prueba;
import com.proyecto.ColegioBackend.service.AcademicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/academico/pruebas")
@CrossOrigin(origins = "*")
public class PruebaController {

    @Autowired
    private AcademicoService academicoService;

    @PostMapping("/curso/{cursoId}")
    public ResponseEntity<?> crearPrueba(
            @PathVariable Long cursoId,
            @RequestBody Prueba prueba,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Map<String, Object> claims = parseToken(token);
            if (claims == null || claims.get("rol") == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autorizado"));
            }
            
            String rol = (String) claims.get("rol");
            Long profesorId = (Long) claims.get("id");
            
            if (!"Profesor".equalsIgnoreCase(rol)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Solo los profesores pueden crear pruebas"));
            }
            
            if (prueba.getAsignatura() == null || prueba.getAsignatura().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La asignatura es requerida"));
            }
            
            // Validar que el profesor esté asignado a esta asignatura en este curso
            com.proyecto.ColegioBackend.model.CursoAsignatura ca = academicoService.obtenerAsignacionPorCursoYAsignatura(cursoId, prueba.getAsignatura());
            if (ca == null || ca.getProfesorId() == null || !ca.getProfesorId().equals(profesorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Usted no es el profesor designado para la asignatura " + prueba.getAsignatura() + " en este curso"));
            }
            
            Prueba nuevaPrueba = academicoService.crearPrueba(cursoId, prueba);
            return new ResponseEntity<>(nuevaPrueba, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> parseToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]), "UTF-8");
                    Long id = null;
                    String rol = null;
                    
                    int idIndex = payload.indexOf("\"id\":");
                    if (idIndex != -1) {
                        int start = idIndex + 5;
                        int end = payload.indexOf(",", start);
                        if (end == -1) end = payload.indexOf("}", start);
                        id = Long.parseLong(payload.substring(start, end).trim());
                    }
                    
                    int rolIndex = payload.indexOf("\"rol\":\"");
                    if (rolIndex != -1) {
                        int start = rolIndex + 7;
                        int end = payload.indexOf("\"", start);
                        rol = payload.substring(start, end);
                    }
                    
                    return Map.of("id", id, "rol", rol);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        return null;
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Prueba>> listarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(academicoService.listarPruebasPorCurso(cursoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prueba> obtenerPorId(@PathVariable Long id) {
        Prueba prueba = academicoService.obtenerPruebaPorId(id);
        return prueba != null ? ResponseEntity.ok(prueba) : ResponseEntity.notFound().build();
    }
}
