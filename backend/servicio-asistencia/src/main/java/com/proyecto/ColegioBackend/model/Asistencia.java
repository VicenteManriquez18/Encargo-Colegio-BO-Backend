package com.proyecto.ColegioBackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "asistencias")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    private String nombreUsuario; // Aquí guardaremos el correo obtenido del otro servicio

    @Column(nullable = false)
    private LocalDateTime fecha;
}