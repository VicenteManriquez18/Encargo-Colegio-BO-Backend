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
            if (estudiante.getApoderado() != null) {
                // Buscamos si el apoderado ya existe por su RUT
                Optional<Apoderado> apoderadoExistente = apoderadoRepository.findByRut(estudiante.getApoderado().getRut());
                
                // Si existe lo asignamos, si no, guardamos el nuevo
                Apoderado apoderado = apoderadoExistente.orElseGet(() -> apoderadoRepository.save(estudiante.getApoderado()));
                estudiante.setApoderado(apoderado);
            }
            return estudianteRepository.save(estudiante);
        } catch (Exception e) {
            logger.error("Error crítico al registrar matrícula para {}: {}", estudiante.getNombre(), e.getMessage());
            throw e; // Lanzamos la excepción para que @Transactional haga el rollback si es necesario
        }
    }

    @Override
    public List<Estudiante> buscarEstudiantesPorCurso(String curso) {
        try {
            return estudianteRepository.findByCurso(curso);
        } catch (Exception e) {
            logger.error("Error al buscar estudiantes del curso {}: {}", curso, e.getMessage());
            return Collections.emptyList();
        }
    }
}
