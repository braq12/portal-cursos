package com.portal.portal_cursos.service.implementation;

import com.portal.portal_cursos.dtos.FinalizarResponse;
import com.portal.portal_cursos.jpa.entity.*;
import com.portal.portal_cursos.jpa.repository.*;
import com.portal.portal_cursos.service.ICapacitacionProgresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CapacitacionProgresoServiceImpl implements ICapacitacionProgresoService {

    private final CapacitacionRepository capRepo;
    private final UsuarioRepository usuarioRepo;
    private final ProgresoCapacitacionRepository progCapRepo;
    private final ProgresoCursoRepository progCursoRepo;
    private final InsigniaRepository insigniaRepo;

    @Override
    @Transactional
    public FinalizarResponse finalizarCapacitacion(Long usuarioId, Long capacitacionId) {
        CapacitacionEntity cap = capRepo.findById(capacitacionId)
                .orElseThrow(() -> new IllegalArgumentException("Capacitación no encontrada"));
        UsuarioEntity usr = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        CursoEntity curso = cap.getCurso();

        // Asegurar progreso_cursos
        ProgresoCursoEntity progCurso = progCursoRepo.findByUsuarioIdAndCursoId(usuarioId, curso.getId())
                .orElseGet(() -> progCursoRepo.save(
                        ProgresoCursoEntity.builder()
                                .usuario(usr)
                                .curso(curso)
                                .estado("INCOMPLETO")
                                .fechaInicio(Instant.now())
                                .build()
                ));

        // Marcar capacitación como COMPLETA
        ProgresoCapacitacionEntity progCap = progCapRepo.findByUsuarioIdAndCapacitacionId(usuarioId, capacitacionId)
                .orElseGet(() -> progCapRepo.save(
                        ProgresoCapacitacionEntity.builder()
                                .usuario(usr)
                                .capacitacion(cap)
                                .estado("INCOMPLETO")
                                .build()
                ));

        if (!"COMPLETO".equalsIgnoreCase(progCap.getEstado())) {
            progCap.setEstado("COMPLETO");
            progCap.setFechaCompletado(Instant.now());
            progCapRepo.save(progCap);
        }

        validarCursoCompleto result = getValidarCursoCompleto(usuarioId, curso, progCurso, usr);

        return FinalizarResponse.builder()
                .cursoId(curso.getId())
                .capacitacionId(cap.getId())
                .cursoCompletado(result.cursoCompletadoAhora())
                .insigniaEmitida(result.insigniaEmitidaAhora())
                .build();
    }

    private validarCursoCompleto getValidarCursoCompleto(Long usuarioId, CursoEntity curso, ProgresoCursoEntity progCurso, UsuarioEntity usr) {
        // Verificar si todas las capacitaciones del curso están completas
        int totalCaps = capRepo.countByCursoId(curso.getId());
        int capsCompletas = progCapRepo.countByUsuarioIdAndCapacitacion_Curso_IdAndEstado(usuarioId, curso.getId(), "COMPLETO");

        boolean cursoCompletadoAhora = false;
        boolean insigniaEmitidaAhora = false;

        if (totalCaps > 0 && capsCompletas >= totalCaps) {
            if (!"COMPLETO".equalsIgnoreCase(progCurso.getEstado())) {
                progCurso.setEstado("COMPLETO");
                progCurso.setFechaCompletado(Instant.now());
                progCursoRepo.save(progCurso);
                cursoCompletadoAhora = true;
            }

            // Emitir insignia si no la tiene
            if (!insigniaRepo.existsByUsuarioIdAndCursoId(usuarioId, curso.getId())) {
                InsigniaEntity ins = InsigniaEntity.builder()
                        .usuario(usr)
                        .curso(curso)
                        .nombre("Completó el curso: " + curso.getTitulo())
                        .urlImagen(null) // puedes asignar un ícono por defecto
                        .build();
                insigniaRepo.save(ins);
                insigniaEmitidaAhora = true;
            }
        }
        validarCursoCompleto result = new validarCursoCompleto(cursoCompletadoAhora, insigniaEmitidaAhora);
        return result;
    }

    private record validarCursoCompleto(boolean cursoCompletadoAhora, boolean insigniaEmitidaAhora) {
    }
}
