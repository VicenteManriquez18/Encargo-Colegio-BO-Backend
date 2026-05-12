package com.proyecto.ColegioBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.proyecto.ColegioBackend.model.Estudiante;
import com.proyecto.ColegioBackend.services.EstudianteService;

import java.util.List;

@RestController
@RequestMapping("/api/matricula")
@CrossOrigin(origins = "http://localhost:5173") // Ajustar según seguridad
public class EstudianteController {

    @Autowired
    private EstudianteService matriculaService;

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
}
