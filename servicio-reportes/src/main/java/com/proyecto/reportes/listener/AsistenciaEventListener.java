package com.proyecto.reportes.listener;

import com.proyecto.reportes.entity.ReporteAsistencia;
import com.proyecto.reportes.model.event.AsistenciaRegistradaEvent;
import com.proyecto.reportes.repository.ReporteAsistenciaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class AsistenciaEventListener {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AsistenciaEventListener.class);
    
    @Autowired
    private ReporteAsistenciaRepository reporteAsistenciaRepository;
    
    @RabbitListener(queues = "eventos.asistencia.queue")
    public void handleAsistenciaRegistrada(AsistenciaRegistradaEvent evento) {
        try {
            log.info("Recibido evento de asistencia registrada: {}", evento.getAsistenciaId());
            
            LocalDateTime fechaEvento = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(evento.getFechaEvento()),
                    ZoneId.systemDefault()
            );
            
            ReporteAsistencia reporte = new ReporteAsistencia(
                    evento.getAsistenciaId(),
                    evento.getAlumnoId(),
                    evento.getCursoId(),
                    evento.getPresente(),
                    LocalDateTime.now(),
                    fechaEvento
            );
            
            reporteAsistenciaRepository.save(reporte);
            log.info("Reporte de asistencia guardado exitosamente");
            
        } catch (Exception e) {
            log.error("Error procesando evento de asistencia: ", e);
        }
    }
}
