package com.portal.portal_cursos.service.implementation;

import com.portal.portal_cursos.dtos.ReporteUsuarioCursoDetalleView;
import com.portal.portal_cursos.dtos.ReporteUsuarioCursosView;
import com.portal.portal_cursos.jpa.repository.ReporteRepository;
import com.portal.portal_cursos.service.IReportesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements IReportesService {
    private final ReporteRepository reporteRepo;


    public List<ReporteUsuarioCursosView> cursosPorUsuario() {
        return reporteRepo.resumenPorUsuario();
    }

    public List<ReporteUsuarioCursoDetalleView> detalleCursosPorUsuario() {
        return reporteRepo.detallePorUsuario();
    }
}
