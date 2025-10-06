package com.portal.portal_cursos.controller;

import com.portal.portal_cursos.dtos.ReporteUsuarioCursoDetalleView;
import com.portal.portal_cursos.dtos.ReporteUsuarioCursosView;
import com.portal.portal_cursos.service.IReportesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final IReportesService reportesService;


    @GetMapping("/cursos-por-usuario")
    public List<ReporteUsuarioCursosView> cursosPorUsuario() {
        return reportesService.cursosPorUsuario();
    }

    @GetMapping("/cursos-por-usuario/detalle")
    public List<ReporteUsuarioCursoDetalleView> detalle() {
        return reportesService.detalleCursosPorUsuario();
    }
}
