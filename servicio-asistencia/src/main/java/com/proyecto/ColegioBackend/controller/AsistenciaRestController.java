package com.proyecto.ColegioBackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.ColegioBackend.model.Asistencia;
import com.proyecto.ColegioBackend.services.AsistenciaService;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // Permitir peticiones desde tu frontend Vite
@RequestMapping("/api/asistencia-admin")
public class AsistenciaRestController {

    @Autowired
    private AsistenciaService asistenciaService;

    @GetMapping
    public List<Asistencia> listar() {
        return asistenciaService.listarTodas();
    }

    @PostMapping
    public ResponseEntity<Asistencia> guardar(@RequestBody Asistencia asistencia) {
        return new ResponseEntity<>(asistenciaService.guardar(asistencia), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> buscarPorId(@PathVariable Long id) {
        Asistencia asistencia = asistenciaService.buscarPorId(id);
        if (asistencia != null) {
            return ResponseEntity.ok(asistencia);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asistenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}