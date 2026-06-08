package com.proyecto.ColegioBackend.controller;

import com.proyecto.ColegioBackend.model.Curso;
import com.proyecto.ColegioBackend.service.AcademicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/academico/cursos")
@CrossOrigin(origins = "http://localhost:5173")
public class CursoController {

    @Autowired
    private AcademicoService academicoService;

    @GetMapping
    public ResponseEntity<List<Curso>> listarCursos() {
        return ResponseEntity.ok(academicoService.listarCursos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> obtenerCursoPorId(@PathVariable Long id) {
        Curso curso = academicoService.obtenerCursoPorId(id);
        return curso != null ? ResponseEntity.ok(curso) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Curso> crearCurso(@RequestBody Curso curso) {
        return new ResponseEntity<>(academicoService.crearCurso(curso), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/profesor")
    public ResponseEntity<?> asignarProfesor(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Object profIdObj = body.get("profesorId");
            if (profIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El campo profesorId es requerido"));
            }
            Long profesorId = Long.valueOf(profIdObj.toString());
            Curso curso = academicoService.asignarProfesor(id, profesorId);
            return ResponseEntity.ok(curso);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
