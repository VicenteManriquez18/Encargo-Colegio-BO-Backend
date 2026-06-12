package com.proyecto.mensajeria.controller;

import com.proyecto.mensajeria.config.RabbitMQConfig;
import com.proyecto.mensajeria.entity.Mensaje;
import com.proyecto.mensajeria.repository.MensajeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/mensajes")
@Tag(name = "Mensajería", description = "Endpoints para la comunicación entre profesores y apoderados")
public class MensajeController {

    private static final Logger log = LoggerFactory.getLogger(MensajeController.class);

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private WebClient webClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${servicio.usuarios.url}")
    private String usuariosServiceUrl;

    @SuppressWarnings("unchecked")
    private Map<String, Object> obtenerUsuarioConCircuitBreaker(Long id) {
        String url = usuariosServiceUrl + "/api/usuarios/" + id;
        try {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception throwable) {
            log.error("Falló la conexión con el servicio de usuarios para el ID {}. Detalle: {}", id, throwable.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> obtenerUsuariosPorRolConCircuitBreaker(String rol) {
        String url = usuariosServiceUrl + "/api/usuarios?rol=" + rol;
        try {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
        } catch (Exception throwable) {
            log.error("Falló la conexión con el servicio de usuarios para listar rol {}. Detalle: {}", rol, throwable.getMessage());
            return Collections.emptyList();
        }
    }

    @PostMapping("/enviar")
    @Operation(summary = "Enviar mensaje a través de RabbitMQ", description = "Valida que la comunicación sea exclusiva entre Profesor y Apoderado y encola el mensaje.")
    public ResponseEntity<?> enviarMensaje(@RequestBody Map<String, Object> body) {
        try {
            Object remitenteIdObj = body.get("remitenteId");
            Object destinatarioIdObj = body.get("destinatarioId");
            String contenido = (String) body.get("contenido");

            if (remitenteIdObj == null || destinatarioIdObj == null || contenido == null || contenido.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Los campos remitenteId, destinatarioId y contenido son requeridos."));
            }

            Long remitenteId = Long.valueOf(remitenteIdObj.toString());
            Long destinatarioId = Long.valueOf(destinatarioIdObj.toString());

            // 1. Obtener emisor y receptor usando Circuit Breaker
            Map<String, Object> emisor = obtenerUsuarioConCircuitBreaker(remitenteId);
            Map<String, Object> receptor = obtenerUsuarioConCircuitBreaker(destinatarioId);

            if (emisor == null || receptor == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of("error", "No se pudo verificar la información de los usuarios debido a que el servicio de usuarios no responde."));
            }

            String emisorRol = (String) emisor.get("rol");
            String receptorRol = (String) receptor.get("rol");
            String emisorCorreo = (String) emisor.get("correo");
            String receptorCorreo = (String) receptor.get("correo");

            // 2. Validar que la comunicación sea Profesor <-> Apoderado exclusivamente
            boolean esEmisorProfesor = "Profesor".equalsIgnoreCase(emisorRol);
            boolean esEmisorApoderado = "Apoderado".equalsIgnoreCase(emisorRol);
            boolean esReceptorProfesor = "Profesor".equalsIgnoreCase(receptorRol);
            boolean esReceptorApoderado = "Apoderado".equalsIgnoreCase(receptorRol);

            boolean esValido = (esEmisorProfesor && esReceptorApoderado) || (esEmisorApoderado && esReceptorProfesor);

            if (!esValido) {
                return ResponseEntity.badRequest().body(Map.of("error", 
                        "La mensajería está restringida exclusivamente para la comunicación entre un Profesor y un Apoderado."));
            }

            // 3. Crear el mensaje
            Mensaje mensaje = Mensaje.builder()
                    .remitenteId(remitenteId)
                    .remitenteNombre(emisorCorreo)
                    .remitenteRol(emisorRol)
                    .destinatarioId(destinatarioId)
                    .destinatarioNombre(receptorCorreo)
                    .destinatarioRol(receptorRol)
                    .contenido(contenido)
                    .fechaEnvio(LocalDateTime.now())
                    .build();

            // 4. Encolar en RabbitMQ
            String routingKey = "chat." + receptorRol.toLowerCase();
            try {
                rabbitTemplate.convertAndSend(RabbitMQConfig.CHAT_EXCHANGE, routingKey, mensaje);
                log.info("Mensaje encolado en RabbitMQ con routingKey: {} de {} para {}", routingKey, emisorCorreo, receptorCorreo);
            } catch (Exception amqpEx) {
                log.warn("ADVERTENCIA: No se pudo enviar el mensaje a RabbitMQ (¿servidor caído?). Guardando directamente en base de datos como fallback. Detalle: {}", amqpEx.getMessage());
                mensajeRepository.save(mensaje);
            }

            return ResponseEntity.ok(Map.of(
                    "status", "Mensaje enviado correctamente",
                    "data", mensaje
            ));

        } catch (Exception e) {
            log.error("Error al enviar mensaje: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al enviar mensaje: " + e.getMessage()));
        }
    }

    @GetMapping("/historial")
    @Operation(summary = "Obtener el historial de chat", description = "Retorna la lista ordenada de mensajes entre dos usuarios.")
    public ResponseEntity<List<Mensaje>> obtenerHistorial(@RequestParam Long user1, @RequestParam Long user2) {
        List<Mensaje> historial = mensajeRepository.findChatHistory(user1, user2);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/contactos/{userId}")
    @Operation(summary = "Obtener contactos permitidos", description = "Retorna apoderados si el usuario es profesor, o profesores si el usuario es apoderado.")
    public ResponseEntity<List<Map<String, Object>>> obtenerContactos(@PathVariable Long userId) {
        Map<String, Object> usuario = obtenerUsuarioConCircuitBreaker(userId);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Collections.emptyList());
        }

        String rol = (String) usuario.get("rol");
        if ("Profesor".equalsIgnoreCase(rol)) {
            // Retorna todos los apoderados
            return ResponseEntity.ok(obtenerUsuariosPorRolConCircuitBreaker("Apoderado"));
        } else if ("Apoderado".equalsIgnoreCase(rol)) {
            // Retorna todos los profesores
            return ResponseEntity.ok(obtenerUsuariosPorRolConCircuitBreaker("Profesor"));
        }

        return ResponseEntity.ok(Collections.emptyList());
    }
}
