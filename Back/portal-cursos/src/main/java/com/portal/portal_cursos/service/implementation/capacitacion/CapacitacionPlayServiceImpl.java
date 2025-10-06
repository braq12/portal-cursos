package com.portal.portal_cursos.service.implementation.capacitacion;

import com.portal.portal_cursos.dtos.capacitacion.IniciarCapacitacionResponse;
import com.portal.portal_cursos.enums.EstadoCursosEnum;
import com.portal.portal_cursos.jpa.entity.*;
import com.portal.portal_cursos.jpa.repository.*;
import com.portal.portal_cursos.service.IAlmacenamientoService;
import com.portal.portal_cursos.service.ICapacitacionPlayService;
import com.portal.portal_cursos.utilities.Constantes;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> new IllegalArgumentException(Constantes.CAPACITACIÓN_NO_ENCONTRADA));

        UsuarioEntity usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(Constantes.USUARIO_NO_ENCONTRADO));

        CursoEntity curso = cap.getCurso();

        // Asegurar progreso_cursos (idempotente).
        progCursoRepo.findByUsuarioIdAndCursoId(usuarioId, curso.getId())
                .orElseGet(() -> progCursoRepo.save(
                        ProgresoCursoEntity.builder()
                                .usuario(usuario)
                                .curso(curso)
                                .estado(EstadoCursosEnum.EN_PROGRESO.getDescripcion())
                                .fechaInicio(Instant.now())
                                .build()));

        // Crear o recuperar progreso_capacitaciones (idempotente)
        var existente = progCapRepo.findByUsuarioIdAndCapacitacionId(usuarioId, capacitacionId);
        ProgresoCapacitacionEntity prog = existente.orElseGet(() -> {
            ProgresoCapacitacionEntity nuevo = ProgresoCapacitacionEntity.builder()
                    .usuario(usuario)
                    .capacitacion(cap)
                    .estado(EstadoCursosEnum.EN_PROGRESO.getDescripcion())
                    .build();
            return progCapRepo.save(nuevo);
        });


        // Pre-signed URL con vigencia
        int mins = cap.getDuracionMinutos();
        String key = cap.getKeyS3();
        String url = storage.urlTemporal(key, mins)
                .orElseThrow(() -> new IllegalStateException(Constantes.NO_FUE_POSIBLE_GENERAR_URL_TEMPORAL));

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

