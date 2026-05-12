package com.proyecto.ColegioBackend.services;

import com.proyecto.ColegioBackend.model.Apoderado;
import com.proyecto.ColegioBackend.model.Estudiante;
import com.proyecto.ColegioBackend.repository.ApoderadoRepository;
import com.proyecto.ColegioBackend.repository.EstudianteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("null")
@Service
public class MatriculaServiceImpl implements EstudianteService {

    private static final Logger logger = LoggerFactory.getLogger(MatriculaServiceImpl.class);

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ApoderadoRepository apoderadoRepository;

    @Override
    public List<Estudiante> listarTodosEstudiantes() {
        try {
            return estudianteRepository.findAll();
        } catch (Exception e) {
            logger.error("Error al obtener la lista de estudiantes: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public Estudiante registrarMatriculaCompleta(Estudiante estudiante) {
        try {
            Apoderado apoderadoEntrada = estudiante.getApoderado();
            if (apoderadoEntrada != null) {
                // Buscamos si el apoderado ya existe por su RUT
                Optional<Apoderado> apoderadoExistente = apoderadoRepository.findByRut(apoderadoEntrada.getRut());
                
                // Si existe lo asignamos, si no, guardamos el nuevo
                Apoderado apoderado = apoderadoExistente.orElseGet(() -> apoderadoRepository.save(apoderadoEntrada));
                estudiante.setApoderado(apoderado);
            }
            return estudianteRepository.save(estudiante);
        } catch (Exception e) {
            logger.error("Error crítico al registrar matrícula para {}: {}", estudiante.getNombre(), e.getMessage());
            throw e; // Lanzamos la excepción para que @Transactional haga el rollback si es necesario
        }
    }

    @Override
    public Estudiante buscarPorId(Long id) {
        try {
            return estudianteRepository.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error("Error al buscar estudiante con ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    @Override
    public Estudiante buscarEstudiantePorRut(String rut) {
        try {
            // Se asume que el repositorio tiene el método findByRut
            return estudianteRepository.findByRut(rut).orElse(null);
        } catch (Exception e) {
            logger.error("Error al buscar estudiante con RUT {}: {}", rut, e.getMessage());
            return null;
        }
    }
}
