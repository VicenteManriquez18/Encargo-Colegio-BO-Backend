package com.proyecto.ColegioBackend.services;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.ColegioBackend.model.Asistencia;
import com.proyecto.ColegioBackend.repository.AsistenciaRepository;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    private static final Logger logger = LoggerFactory.getLogger(AsistenciaServiceImpl.class);

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Override
    public List<Asistencia> listarTodas() {
        try {
            return asistenciaRepository.findAll();
        } catch (Exception e) {
            logger.error("Error al listar todas las asistencias: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Asistencia guardar(Asistencia asistencia) {
        try {
            return asistenciaRepository.save(asistencia);
        } catch (Exception e) {
            logger.error("Error al guardar asistencia para el usuario {}: {}", asistencia.getUsuarioId(), e.getMessage());
            return null;
        }
    }

    @Override
    public Asistencia buscarPorId(Long id) {
        try {
            return asistenciaRepository.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error("Error al buscar asistencia con ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            asistenciaRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error al eliminar asistencia con ID {}: {}", id, e.getMessage());
        }
    }

    @Override
    public List<Asistencia> listarPorUsuario(Long usuarioId) {
        try {
            return asistenciaRepository.findByUsuarioId(usuarioId);
        } catch (Exception e) {
            logger.error("Error al listar asistencias para el usuario {}: {}", usuarioId, e.getMessage());
            return Collections.emptyList();
        }
    }
}