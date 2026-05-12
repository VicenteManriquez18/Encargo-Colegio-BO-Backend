package com.example.servicio_matricula.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.proyecto.ColegioBackend.model.Estudiante;
import com.proyecto.ColegioBackend.repository.EstudianteRepository;
import com.proyecto.ColegioBackend.services.MatriculaServiceImpl;
import com.proyecto.ColegioBackend.repository.ApoderadoRepository;
import com.proyecto.ColegioBackend.model.Apoderado;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
public class MatriculaTests {

    @Mock private EstudianteRepository estudianteRepository;
    @Mock private ApoderadoRepository apoderadoRepository;

    @InjectMocks private MatriculaServiceImpl matriculaService;

    private Estudiante estudiante;
    private Apoderado apoderado;

    @BeforeEach
    void setUp() {
        apoderado = new Apoderado();
        apoderado.setId(1L);
        apoderado.setNombre("Juan Perez");
        apoderado.setRut("12.345.678-9");

        estudiante = new Estudiante();
        estudiante.setId(1L);
        estudiante.setNombre("Diego Perez");
        estudiante.setRut("23.456.789-0");
        estudiante.setApoderado(apoderado);
        estudiante.setEstado("Activo");
    }

    @Test
    void listarTodosEstudiantes_DebeRetornarLista() {
        when(estudianteRepository.findAll()).thenReturn(Arrays.asList(estudiante));

        List<Estudiante> resultado = matriculaService.listarTodosEstudiantes();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(estudianteRepository, times(1)).findAll();
    }

    @Test
    void listarTodosEstudiantes_CuandoHayError_DebeRetornarListaVacia() {
        when(estudianteRepository.findAll()).thenThrow(new RuntimeException("Error de conexión"));

        List<Estudiante> resultado = matriculaService.listarTodosEstudiantes();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorId_CuandoExiste_DebeRetornarEstudiante() {
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));

        Estudiante resultado = matriculaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_CuandoNoExiste_DebeRetornarNull() {
        when(estudianteRepository.findById(99L)).thenReturn(Optional.empty());

        Estudiante resultado = matriculaService.buscarPorId(99L);

        assertNull(resultado);
    }

    @Test
    void buscarEstudiantePorRut_CuandoExiste_DebeRetornarEstudiante() {
        when(estudianteRepository.findByRut("23.456.789-0")).thenReturn(Optional.of(estudiante));

        Estudiante resultado = matriculaService.buscarEstudiantePorRut("23.456.789-0");

        assertNotNull(resultado);
        assertEquals("23.456.789-0", resultado.getRut());
    }

    @Test
    void registrarMatriculaCompleta_CuandoApoderadoEsNuevo_DebeGuardarAmbos() {
        // Simulamos que el apoderado NO existe en la BD
        when(apoderadoRepository.findByRut(any())).thenReturn(Optional.empty());
        when(apoderadoRepository.save(any())).thenReturn(apoderado);
        when(estudianteRepository.save(any(Estudiante.class))).thenReturn(estudiante);

        Estudiante resultado = matriculaService.registrarMatriculaCompleta(estudiante);

        assertNotNull(resultado);
        verify(apoderadoRepository, times(1)).save(any(Apoderado.class));
        verify(estudianteRepository, times(1)).save(estudiante);
    }

    @Test
    void registrarMatriculaCompleta_CuandoApoderadoYaExiste_DebeReutilizarlo() {
        // Simulamos que el apoderado SI existe
        when(apoderadoRepository.findByRut(apoderado.getRut())).thenReturn(Optional.of(apoderado));
        when(estudianteRepository.save(any(Estudiante.class))).thenReturn(estudiante);

        Estudiante resultado = matriculaService.registrarMatriculaCompleta(estudiante);

        assertNotNull(resultado);
        assertNotNull(resultado.getApoderado());
        assertEquals(apoderado.getId(), resultado.getApoderado().getId());
        // Verificamos que NO se llamó al save de apoderado porque ya existía
        verify(apoderadoRepository, never()).save(any(Apoderado.class));
        verify(estudianteRepository).save(estudiante);
    }
}
