package com.proyecto.ColegioBackend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.ColegioBackend.model.Asistencia;
import com.proyecto.ColegioBackend.repository.AsistenciaRepository;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Override
    public List<Asistencia> listarTodas() {
        return asistenciaRepository.findAll();
    }

    @Override
    public Asistencia guardar(Asistencia asistencia) {
        return asistenciaRepository.save(asistencia);
    }

    @Override
    public Asistencia buscarPorId(Long id) {
        return asistenciaRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        asistenciaRepository.deleteById(id);
    }
}