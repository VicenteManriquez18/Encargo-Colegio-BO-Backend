package com.proyecto.ColegioBackend.services;

import com.proyecto.ColegioBackend.model.Apoderado;
import com.proyecto.ColegioBackend.model.Estudiante;
import com.proyecto.ColegioBackend.repository.ApoderadoRepository;
import com.proyecto.ColegioBackend.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MatriculaServiceImpl implements EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ApoderadoRepository apoderadoRepository;

    @Override
    public List<Estudiante> listarTodosEstudiantes() {
        return estudianteRepository.findAll();
    }

    @Override
    @Transactional
    public Estudiante registrarMatriculaCompleta(Estudiante estudiante) {
        if (estudiante.getApoderado() != null) {
            // Buscamos si el apoderado ya existe por su RUT
            Optional<Apoderado> apoderadoExistente = apoderadoRepository.findByRut(estudiante.getApoderado().getRut());
            
            // Si existe lo asignamos, si no, guardamos el nuevo
            Apoderado apoderado = apoderadoExistente.orElseGet(() -> apoderadoRepository.save(estudiante.getApoderado()));
            estudiante.setApoderado(apoderado);
        }
        return estudianteRepository.save(estudiante);
    }

    @Override
    public List<Estudiante> buscarEstudiantesPorCurso(String curso) {
        return estudianteRepository.findByCurso(curso);
    }
}
