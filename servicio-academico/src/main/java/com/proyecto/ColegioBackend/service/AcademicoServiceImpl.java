package com.proyecto.ColegioBackend.service;

import com.proyecto.ColegioBackend.model.Curso;
import com.proyecto.ColegioBackend.model.Matricula;
import com.proyecto.ColegioBackend.model.Prueba;
import com.proyecto.ColegioBackend.model.Nota;
import com.proyecto.ColegioBackend.repository.CursoRepository;
import com.proyecto.ColegioBackend.repository.MatriculaRepository;
import com.proyecto.ColegioBackend.repository.PruebaRepository;
import com.proyecto.ColegioBackend.repository.NotaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AcademicoServiceImpl implements AcademicoService {

    private static final Logger logger = LoggerFactory.getLogger(AcademicoServiceImpl.class);

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private PruebaRepository pruebaRepository;

    @Autowired
    private NotaRepository notaRepository;

    // ==================== CURSOS ====================

    @Override
    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    @Override
    public Curso obtenerCursoPorId(Long id) {
        return cursoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Curso crearCurso(Curso curso) {
        if (curso.getCodigo() == null || curso.getCodigo().isEmpty()) {
            // Reemplaza caracteres extraños por guiones
            String safeName = curso.getNombre().toUpperCase().replaceAll("[^A-Z0-9]", "-");
            curso.setCodigo("CUR-" + safeName);
        }
        return cursoRepository.save(curso);
    }

    @Override
    @Transactional
    public Curso asignarProfesor(Long cursoId, Long profesorId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + cursoId));
        curso.setProfesorId(profesorId);
        return cursoRepository.save(curso);
    }

    @Override
    public Curso obtenerCursoPorNombre(String nombre) {
        return cursoRepository.findByNombre(nombre).orElse(null);
    }

    // ==================== MATRÍCULAS ====================

    @Override
    @Transactional
    public Matricula matricularAlumno(Long usuarioId, Long cursoId) {
        // Buscar el curso por ID
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + cursoId));

        // Verificar si ya está matriculado en este curso
        Optional<Matricula> matriculaExistente = matriculaRepository.findByCursoIdAndUsuarioId(curso.getId(), usuarioId);
        if (matriculaExistente.isPresent()) {
            return matriculaExistente.get();
        }

        // Crear la matrícula
        Matricula matricula = Matricula.builder()
                .curso(curso)
                .usuarioId(usuarioId)
                .build();

        return matriculaRepository.save(matricula);
    }

    @Override
    public List<Matricula> listarMatriculasPorCurso(Long cursoId) {
        return matriculaRepository.findByCursoId(cursoId);
    }

    @Override
    public List<Matricula> listarMatriculasPorAlumno(Long usuarioId) {
        return matriculaRepository.findByUsuarioId(usuarioId);
    }

    // ==================== PRUEBAS ====================

    @Override
    @Transactional
    public Prueba crearPrueba(Long cursoId, Prueba prueba) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + cursoId));
        prueba.setCurso(curso);
        return pruebaRepository.save(prueba);
    }

    @Override
    public List<Prueba> listarPruebasPorCurso(Long cursoId) {
        return pruebaRepository.findByCursoId(cursoId);
    }

    @Override
    public Prueba obtenerPruebaPorId(Long id) {
        return pruebaRepository.findById(id).orElse(null);
    }

    // ==================== NOTAS ====================

    @Override
    @Transactional
    public Nota registrarNota(Long pruebaId, Long alumnoId, Double valor, String comentario) {
        if (valor < 1.0 || valor > 7.0) {
            throw new IllegalArgumentException("El valor de la nota debe estar entre 1.0 y 7.0");
        }

        Prueba prueba = pruebaRepository.findById(pruebaId)
                .orElseThrow(() -> new RuntimeException("Prueba no encontrada con ID: " + pruebaId));

        // Verificar si ya existe una nota para este alumno en esta prueba
        Optional<Nota> notaExistente = notaRepository.findByPruebaIdAndAlumnoId(pruebaId, alumnoId);
        
        Nota nota;
        if (notaExistente.isPresent()) {
            nota = notaExistente.get();
            nota.setValor(valor);
            nota.setComentario(comentario);
        } else {
            nota = Nota.builder()
                    .prueba(prueba)
                    .alumnoId(alumnoId)
                    .valor(valor)
                    .comentario(comentario)
                    .build();
        }

        return notaRepository.save(nota);
    }

    @Override
    public List<Nota> listarNotasPorPrueba(Long pruebaId) {
        return notaRepository.findByPruebaId(pruebaId);
    }

    @Override
    public List<Nota> listarNotasPorAlumno(Long alumnoId) {
        return notaRepository.findByAlumnoId(alumnoId);
    }
}
