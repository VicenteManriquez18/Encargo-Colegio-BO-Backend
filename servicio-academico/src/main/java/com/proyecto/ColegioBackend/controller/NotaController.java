package com.proyecto.ColegioBackend.controller;

import com.proyecto.ColegioBackend.model.Nota;
import com.proyecto.ColegioBackend.service.AcademicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/academico/notas")
@CrossOrigin(origins = "http://localhost:5173")
public class NotaController {

    @Autowired
    private AcademicoService academicoService;

    @PostMapping("/prueba/{pruebaId}")
    public ResponseEntity<?> registrarNota(@PathVariable Long pruebaId, @RequestBody Map<String, Object> body) {
        try {
            Object alumnoIdObj = body.get("alumnoId");
            Object valorObj = body.get("valor");
            String comentarioObj = body.containsKey("comentario") ? (String) body.get("comentario") : null;

            if (alumnoIdObj == null || valorObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Los campos alumnoId y valor son requeridos"));
            }

            Long alumnoId = Long.valueOf(alumnoIdObj.toString());
            Double valor = Double.valueOf(valorObj.toString());

            Nota nota = academicoService.registrarNota(pruebaId, alumnoId, valor, comentarioObj);
            return new ResponseEntity<>(nota, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/prueba/{pruebaId}")
    public ResponseEntity<List<Nota>> listarPorPrueba(@PathVariable Long pruebaId) {
        return ResponseEntity.ok(academicoService.listarNotasPorPrueba(pruebaId));
    }

    @GetMapping("/alumno/{alumnoId}")
    public ResponseEntity<List<Nota>> listarPorAlumno(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(academicoService.listarNotasPorAlumno(alumnoId));
    }
}
