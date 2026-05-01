package com.proyecto.ColegioBackendTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

import com.proyecto.ColegioBackend.model.Usuario;
import com.proyecto.ColegioBackend.repository.UsuarioRepository;
import com.proyecto.ColegioBackend.services.UsuarioServiceImpl;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .correo("test@example.com")
                .password("password123")
                .rol("USER")
                .build();
    }

    @Test
    void testListarTodos() {
        // Given
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // When
        List<Usuario> result = usuarioService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(usuario, result.get(0));
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testGuardar() {
        // Given
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario result = usuarioService.guardar(usuario);

        // Then
        assertNotNull(result);
        assertEquals(usuario.getId(), result.getId());
        assertEquals(usuario.getCorreo(), result.getCorreo());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testBuscarPorId_UsuarioExiste() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // When
        Usuario result = usuarioService.buscarPorId(1L);

        // Then
        assertNotNull(result);
        assertEquals(usuario.getId(), result.getId());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_UsuarioNoExiste() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Usuario result = usuarioService.buscarPorId(1L);

        // Then
        assertNull(result);
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testEliminar() {
        // Given
        doNothing().when(usuarioRepository).deleteById(1L);

        // When
        usuarioService.eliminar(1L);

        // Then
        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}