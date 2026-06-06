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
@CrossOrigin(origins = "http://localhost:5173")
public class PruebaController {

    @Autowired
    private AcademicoService academicoService;

    @PostMapping("/curso/{cursoId}")
    public ResponseEntity<?> crearPrueba(@PathVariable Long cursoId, @RequestBody Prueba prueba) {
        try {
            Prueba nuevaPrueba = academicoService.crearPrueba(cursoId, prueba);
            return new ResponseEntity<>(nuevaPrueba, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
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
