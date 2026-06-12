package com.proyecto.ColegioBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import com.proyecto.ColegioBackend.model.Asistencia;
import com.proyecto.ColegioBackend.repository.AsistenciaRepository;
import com.proyecto.ColegioBackend.services.AsistenciaService;
import java.util.Map;
import java.util.List;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/asistencia")
public class AsistenciaController {

    @Autowired
    private AsistenciaRepository asistenciaRepository;
    
    @Autowired
    private AsistenciaService asistenciaService;

    // Instanciamos WebClient para comunicación entre microservicios
    @Autowired
    private WebClient webClient;

    @Value("${servicio.usuarios.url}")
    private String usuariosUrl;

    @Value("${servicio.matricula.url}")
    private String matriculaUrl;

    @Value("${servicio.academico.url}")
    private String academicoUrl;

    // ==================== GET ====================
    
    @GetMapping
    public List<Asistencia> listar() {
        return asistenciaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> buscarPorId(@PathVariable Long id) {
        Asistencia asistencia = asistenciaService.buscarPorId(id);
        if (asistencia != null) {
            return ResponseEntity.ok(asistencia);
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== POST ====================
    
    @PostMapping
    public ResponseEntity<?> guardar(
            @RequestBody Asistencia asistencia,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Map<String, Object> claims = parseToken(token);
        if (claims == null || claims.get("rol") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autorizado"));
        }
        
        String rol = (String) claims.get("rol");
        Long profesorId = (Long) claims.get("id");
        
        if (!"Profesor".equalsIgnoreCase(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Solo los profesores pueden registrar asistencia"));
        }
        
        if (asistencia.getCursoId() == null || asistencia.getAsignatura() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Los campos 'cursoId' y 'asignatura' son requeridos"));
        }
        
        // 1. Validar que el profesor esté asignado a la asignatura en este curso
        if (!verificarAsignacionProfesor(asistencia.getCursoId(), asistencia.getAsignatura(), profesorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Usted no es el profesor designado para la asignatura '" + asistencia.getAsignatura() + "' en este curso"));
        }
        
        // 2. Validar que el estudiante esté matriculado en el curso
        if (!verificarMatriculaCurso(asistencia.getUsuarioId(), asistencia.getCursoId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "El estudiante con ID de usuario " + asistencia.getUsuarioId() + " no está matriculado en este curso"));
        }
        
        asistencia.setFecha(LocalDateTime.now());
        Asistencia guardada = asistenciaService.guardar(asistencia);
        return new ResponseEntity<>(guardada, HttpStatus.CREATED);
    }

    private Map<String, Object> parseToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]), "UTF-8");
                    Long id = null;
                    String rol = null;
                    
                    int idIndex = payload.indexOf("\"id\":");
                    if (idIndex != -1) {
                        int start = idIndex + 5;
                        int end = payload.indexOf(",", start);
                        if (end == -1) end = payload.indexOf("}", start);
                        id = Long.parseLong(payload.substring(start, end).trim());
                    }
                    
                    int rolIndex = payload.indexOf("\"rol\":\"");
                    if (rolIndex != -1) {
                        int start = rolIndex + 7;
                        int end = payload.indexOf("\"", start);
                        rol = payload.substring(start, end);
                    }
                    
                    return Map.of("id", id, "rol", rol);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        return null;
    }

    private boolean verificarMatriculaCurso(Long studentUserId, Long cursoId) {
        try {
            String url = matriculaUrl + "/api/matricula/estudiantes/usuario/" + studentUserId;
            java.util.Map response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(java.util.Map.class)
                .block();
            if (response != null && response.containsKey("cursoId")) {
                Object cId = response.get("cursoId");
                if (cId != null) {
                    Long actualCursoId = Long.valueOf(cId.toString());
                    return cursoId.equals(actualCursoId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al verificar matrícula para usuarioId " + studentUserId + ": " + e.getMessage());
        }
        return false;
    }

    private boolean verificarAsignacionProfesor(Long cursoId, String asignatura, Long profesorId) {
        try {
            String url = academicoUrl + "/api/academico/cursos/" + cursoId + "/asignaturas";
            java.util.List response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(java.util.List.class)
                .block();
            if (response != null) {
                for (Object item : response) {
                    if (item instanceof java.util.Map) {
                        java.util.Map map = (java.util.Map) item;
                        String asigName = (String) map.get("asignatura");
                        if (asignatura.equalsIgnoreCase(asigName)) {
                            Object profIdObj = map.get("profesorId");
                            if (profIdObj != null) {
                                Long actualProfesorId = Long.valueOf(profIdObj.toString());
                                return profesorId.equals(actualProfesorId);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al verificar asignación de profesor para cursoId " + cursoId + ": " + e.getMessage());
        }
        return false;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarAsistencia(@RequestBody Map<String, Object> request) {
        Object idObj = request.get("usuarioId");
        if (idObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "El campo usuarioId es requerido"));
        }

        Long usuarioId = Long.valueOf(idObj.toString());

        try {
            String url = usuariosUrl + "/api/usuarios/" + usuarioId;
            ResponseEntity<Map> response = webClient.get()
                .uri(url)
                .retrieve()
                .toEntity(Map.class)
                .block();
            Map<String, Object> usuario = response.getBody();

            if (usuario == null || !usuario.containsKey("correo")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado en el sistema central"));
            }

            Asistencia nuevaAsistencia = new Asistencia();
            nuevaAsistencia.setUsuarioId(usuarioId);
            nuevaAsistencia.setNombreUsuario((String) usuario.get("correo"));
            nuevaAsistencia.setFecha(LocalDateTime.now());

            return ResponseEntity.ok(asistenciaService.guardar(nuevaAsistencia));
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "ID de usuario inexistente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al conectar con el servicio de usuarios: " + e.getMessage()));
        }
    }

    // ==================== GET ALUMNOS DESDE USUARIOS ====================

    @GetMapping("/alumnos")
    public ResponseEntity<?> obtenerAlumnos(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            String url = usuariosUrl + "/api/usuarios";
            WebClient.RequestHeadersSpec<?> request = webClient.get().uri(url);
            if (token != null) {
                request = request.header("Authorization", token);
            }
            ResponseEntity<List> response = request.retrieve().toEntity(List.class).block();
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al conectar con el servicio de usuarios: " + e.getMessage()));
        }
    }

    // ==================== GET BY ALUMNO ====================

    @GetMapping("/alumno/{usuarioId}")
    public ResponseEntity<List<Asistencia>> buscarPorUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(asistenciaService.listarPorUsuario(usuarioId));
    }

    // ==================== DELETE ====================
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asistenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}