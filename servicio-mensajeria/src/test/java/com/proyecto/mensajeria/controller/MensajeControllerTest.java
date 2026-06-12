package com.proyecto.mensajeria.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import com.proyecto.mensajeria.entity.Mensaje;
import com.proyecto.mensajeria.repository.MensajeRepository;

@ExtendWith(MockitoExtension.class)
public class MensajeControllerTest {

    @Mock
    private MensajeRepository mensajeRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MensajeController mensajeController;

    private String usuariosServiceUrl = "http://localhost:8081";

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(mensajeController, "usuariosServiceUrl", usuariosServiceUrl);
        
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @SuppressWarnings("unchecked")
    @Test
    void enviarMensaje_ProfesorParaApoderado_Exito() {
        Map<String, Object> body = new HashMap<>();
        body.put("remitenteId", 1L);
        body.put("destinatarioId", 2L);
        body.put("contenido", "Hola, ¿cómo está?");

        Map<String, Object> emisor = Map.of("id", 1L, "rol", "Profesor", "correo", "profe@colegio.com");
        Map<String, Object> receptor = Map.of("id", 2L, "rol", "Apoderado", "correo", "apoderado@colegio.com");

        when(responseSpec.bodyToMono(Map.class))
                .thenReturn(Mono.just(emisor))
                .thenReturn(Mono.just(receptor));

        ResponseEntity<?> response = mensajeController.enviarMensaje(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(Mensaje.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void enviarMensaje_ApoderadoParaProfesor_Exito() {
        Map<String, Object> body = new HashMap<>();
        body.put("remitenteId", 2L);
        body.put("destinatarioId", 1L);
        body.put("contenido", "Hola, todo bien.");

        Map<String, Object> emisor = Map.of("id", 2L, "rol", "Apoderado", "correo", "apoderado@colegio.com");
        Map<String, Object> receptor = Map.of("id", 1L, "rol", "Profesor", "correo", "profe@colegio.com");

        when(responseSpec.bodyToMono(Map.class))
                .thenReturn(Mono.just(emisor))
                .thenReturn(Mono.just(receptor));

        ResponseEntity<?> response = mensajeController.enviarMensaje(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(Mensaje.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void enviarMensaje_ProfesorParaProfesor_DebeRetornarBadRequest() {
        Map<String, Object> body = new HashMap<>();
        body.put("remitenteId", 1L);
        body.put("destinatarioId", 3L);
        body.put("contenido", "Hola colega");

        Map<String, Object> emisor = Map.of("id", 1L, "rol", "Profesor", "correo", "profe1@colegio.com");
        Map<String, Object> receptor = Map.of("id", 3L, "rol", "Profesor", "correo", "profe2@colegio.com");

        when(responseSpec.bodyToMono(Map.class))
                .thenReturn(Mono.just(emisor))
                .thenReturn(Mono.just(receptor));

        ResponseEntity<?> response = mensajeController.enviarMensaje(body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("La mensajería está restringida exclusivamente"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void enviarMensaje_ServicioUsuariosCaido_DebeRetornarServiceUnavailable() {
        Map<String, Object> body = new HashMap<>();
        body.put("remitenteId", 1L);
        body.put("destinatarioId", 2L);
        body.put("contenido", "Hola");

        when(responseSpec.bodyToMono(Map.class))
                .thenReturn(Mono.error(new RuntimeException("Error de conexión")));

        ResponseEntity<?> response = mensajeController.enviarMensaje(body);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("No se pudo verificar la información"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void enviarMensaje_RabbitMQCaido_FallbackParaGuardarEnBDDirectamente() {
        Map<String, Object> body = new HashMap<>();
        body.put("remitenteId", 1L);
        body.put("destinatarioId", 2L);
        body.put("contenido", "Hola, RabbitMQ está caído");

        Map<String, Object> emisor = Map.of("id", 1L, "rol", "Profesor", "correo", "profe@colegio.com");
        Map<String, Object> receptor = Map.of("id", 2L, "rol", "Apoderado", "correo", "apoderado@colegio.com");

        when(responseSpec.bodyToMono(Map.class))
                .thenReturn(Mono.just(emisor))
                .thenReturn(Mono.just(receptor));

        doThrow(new RuntimeException("RabbitMQ Connection Refused"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Mensaje.class));

        ResponseEntity<?> response = mensajeController.enviarMensaje(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mensajeRepository, times(1)).save(any(Mensaje.class));
    }
}
