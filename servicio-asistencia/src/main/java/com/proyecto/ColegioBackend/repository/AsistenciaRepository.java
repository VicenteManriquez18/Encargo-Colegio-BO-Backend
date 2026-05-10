package com.proyecto.ColegioBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.ColegioBackend.model.Asistencia;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
}