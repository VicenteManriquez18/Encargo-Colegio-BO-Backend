package com.proyecto.reportes.listener;

import com.proyecto.reportes.entity.ReporteMatricula;
import com.proyecto.reportes.model.event.MatriculaRegistradaEvent;
import com.proyecto.reportes.repository.ReporteMatriculaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class MatriculaEventListener {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MatriculaEventListener.class);
    
    @Autowired
    private ReporteMatriculaRepository reporteMatriculaRepository;
    
    @RabbitListener(queues = "eventos.matricula.queue")
    public void handleMatriculaRegistrada(MatriculaRegistradaEvent evento) {
        try {
            log.info("Recibido evento de matrícula registrada: {}", evento.getMatriculaId());
            
            LocalDateTime fechaEvento = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(evento.getFechaEvento()),
                    ZoneId.systemDefault()
            );
            
            ReporteMatricula reporte = new ReporteMatricula(
                    evento.getMatriculaId(),
                    evento.getAlumnoId(),
                    evento.getCursoId(),
                    evento.getEstado(),
                    LocalDateTime.now(),
                    fechaEvento
            );
            
            reporteMatriculaRepository.save(reporte);
            log.info("Reporte de matrícula guardado exitosamente");
            
        } catch (Exception e) {
            log.error("Error procesando evento de matrícula: ", e);
        }
    }
}
