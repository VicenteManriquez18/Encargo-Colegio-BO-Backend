package com.proyecto.ColegioBackend.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.proyecto.ColegioBackend.model.Asistencia;
import com.proyecto.ColegioBackend.repository.AsistenciaRepository;

@ExtendWith(MockitoExtension.class)
public class AsistenciaServiceImplTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @InjectMocks
    private AsistenciaServiceImpl asistenciaService;

    private Asistencia asistencia;

    @BeforeEach
    void setUp() {
        asistencia = new Asistencia();
        asistencia.setId(1L);
        asistencia.setUsuarioId(100L);
        asistencia.setNombreUsuario("usuario@test.com");
        asistencia.setFecha(LocalDateTime.now());
    }

    @Test
    void listarTodas_DebeRetornarListaDeAsistencias() {
        // Arrange
        when(asistenciaRepository.findAll()).thenReturn(Arrays.asList(asistencia));

        // Act
        List<Asistencia> resultado = asistenciaService.listarTodas();

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        verify(asistenciaRepository, times(1)).findAll();
    }

    @Test
    void guardar_DebeRetornarAsistenciaGuardada() {
        // Arrange
        when(asistenciaRepository.save(any(Asistencia.class))).thenReturn(asistencia);

        // Act
        Asistencia resultado = asistenciaService.guardar(asistencia);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("usuario@test.com", resultado.getNombreUsuario());
        verify(asistenciaRepository, times(1)).save(any(Asistencia.class));
    }

    @Test
    void buscarPorId_CuandoExiste_DebeRetornarAsistencia() {
        // Arrange
        when(asistenciaRepository.findById(1L)).thenReturn(Optional.of(asistencia));

        // Act
        Asistencia resultado = asistenciaService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(asistenciaRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_CuandoNoExiste_DebeRetornarNull() {
        // Arrange
        when(asistenciaRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Asistencia resultado = asistenciaService.buscarPorId(2L);

        // Assert
        assertNull(resultado);
        verify(asistenciaRepository, times(1)).findById(2L);
    }

    @Test
    void eliminar_DebeLlamarAlRepositorio() {
        // Arrange
        doNothing().when(asistenciaRepository).deleteById(1L);

        // Act
        asistenciaService.eliminar(1L);

        // Assert
        verify(asistenciaRepository, times(1)).deleteById(1L);
    }
}
