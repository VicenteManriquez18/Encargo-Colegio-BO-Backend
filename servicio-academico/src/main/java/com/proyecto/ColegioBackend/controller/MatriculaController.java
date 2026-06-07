package com.proyecto.ColegioBackend.controller;

import com.proyecto.ColegioBackend.model.Matricula;
import com.proyecto.ColegioBackend.service.AcademicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/academico/matriculas")
@CrossOrigin(origins = "http://localhost:5173")
public class MatriculaController {

    @Autowired
    private AcademicoService academicoService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarMatricula(@RequestBody Map<String, Object> body) {
        try {
            Object usuarioIdObj = body.get("usuarioId");
            Object cursoIdObj = body.get("cursoId");
            
            if (usuarioIdObj == null || cursoIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Los campos usuarioId y cursoId son requeridos"));
            }
            
            Long usuarioId = Long.valueOf(usuarioIdObj.toString());
            Long cursoId = Long.valueOf(cursoIdObj.toString());
            
            Matricula matricula = academicoService.matricularAlumno(usuarioId, cursoId);
            return new ResponseEntity<>(matricula, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Matricula>> listarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(academicoService.listarMatriculasPorCurso(cursoId));
    }

    @GetMapping("/alumno/{usuarioId}")
    public ResponseEntity<List<Matricula>> listarPorAlumno(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(academicoService.listarMatriculasPorAlumno(usuarioId));
    }
}
