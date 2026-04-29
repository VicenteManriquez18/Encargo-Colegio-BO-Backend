package com.proyecto.ColegioBackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/asistencia-web")
public class AsistenciaController {

    @GetMapping("/vista")
    public String mostrarVista() {
        // Retorna el nombre de la plantilla (ej. asistencia.html usando Thymeleaf o JSP)
        return "asistencia";
    }
}