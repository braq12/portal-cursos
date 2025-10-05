// src/main/java/com/portal/portal_cursos/service/implementation/CapacitacionPlayServiceImpl.java
package com.portal.portal_cursos.service.implementation;

import com.portal.portal_cursos.dtos.IniciarCapacitacionResponse;
import com.portal.portal_cursos.jpa.entity.*;
import com.portal.portal_cursos.jpa.repository.*;
import com.portal.portal_cursos.service.ICapacitacionPlayService;
import com.portal.portal_cursos.service.IAlmacenamientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CapacitacionPlayServiceImpl implements ICapacitacionPlayService {

    private final CapacitacionRepository capRepo;
    private final ProgresoCapacitacionRepository progCapRepo;
    private final ProgresoCursoRepository progCursoRepo;
    private final UsuarioRepository usuarioRepo;
    private final CursoRepository cursoRepo;
    private final IAlmacenamientoService storage;


    @Override
    @Transactional
    public IniciarCapacitacionResponse iniciarOContinuar(Long usuarioId, Long capacitacionId) {

        CapacitacionEntity cap = capRepo.findById(capacitacionId)
                .orElseThrow(() -> new IllegalArgumentException("Capacitación no encontrada"));

        UsuarioEntity usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        CursoEntity curso = cap.getCurso();

        // Asegurar progreso_cursos (idempotente).
        progCursoRepo.findByUsuarioIdAndCursoId(usuarioId, curso.getId())
                .orElseGet(() -> progCursoRepo.save(
                        ProgresoCursoEntity.builder()
                                .usuario(usuario)
                                .curso(curso)
                                .estado("INCOMPLETO")
                                .fechaInicio(Instant.now())
                                .build()));

        // Crear o recuperar progreso_capacitaciones (idempotente)
        var existente = progCapRepo.findByUsuarioIdAndCapacitacionId(usuarioId, capacitacionId);
        ProgresoCapacitacionEntity prog = existente.orElseGet(() -> {
            ProgresoCapacitacionEntity nuevo = ProgresoCapacitacionEntity.builder()
                    .usuario(usuario)
                    .capacitacion(cap)
                    .estado("INCOMPLETO")
                    .build();
            return progCapRepo.save(nuevo);
        });


        // Pre-signed URL con vigencia
        int mins = cap.getDuracionMinutos();
        String key = cap.getKeyS3();
        String url = storage.urlTemporal(key, mins)
                .orElseThrow(() -> new IllegalStateException("No fue posible generar URL temporal"));

        return IniciarCapacitacionResponse.builder()
                .cursoId(curso.getId())
                .capacitacionId(cap.getId())
                .titulo(cap.getTitulo())
                .tipo(cap.getTipo())
                .estado(prog.getEstado())
                .url(url)
                .expiraSegundos(mins * 60)
                .build();
    }
}

