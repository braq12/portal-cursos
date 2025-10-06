package com.portal.portal_cursos.service.implementation.capacitacion;

import com.portal.portal_cursos.dtos.capacitacion.FinalizarResponse;
import com.portal.portal_cursos.enums.EstadoCursosEnum;
import com.portal.portal_cursos.jpa.entity.*;
import com.portal.portal_cursos.jpa.repository.*;
import com.portal.portal_cursos.service.ICapacitacionFinalizarService;
import com.portal.portal_cursos.service.IEmailService;
import com.portal.portal_cursos.utilities.Constantes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.portal.portal_cursos.templates.EmailTemplates.cursoCompletado;

@Service
@RequiredArgsConstructor
public class CapacitacionFinalizarServiceImpl implements ICapacitacionFinalizarService {

    private final CapacitacionRepository capRepo;
    private final UsuarioRepository usuarioRepo;
    private final ProgresoCapacitacionRepository progCapRepo;
    private final ProgresoCursoRepository progCursoRepo;
    private final InsigniaRepository insigniaRepo;
    private final IEmailService emailService;

    @Override
    @Transactional
    public FinalizarResponse finalizarCapacitacion(Long usuarioId, Long capacitacionId) {
        CapacitacionEntity cap = capRepo.findById(capacitacionId)
                .orElseThrow(() -> new IllegalArgumentException(Constantes.CAPACITACIÓN_NO_ENCONTRADA));
        UsuarioEntity usr = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(Constantes.USUARIO_NO_ENCONTRADO));
        CursoEntity curso = cap.getCurso();

        // Asegurar progreso_cursos
        ProgresoCursoEntity progCurso = progCursoRepo.findByUsuarioIdAndCursoId(usuarioId, curso.getId())
                .orElseGet(() -> progCursoRepo.save(
                        ProgresoCursoEntity.builder()
                                .usuario(usr)
                                .curso(curso)
                                .estado(EstadoCursosEnum.EN_PROGRESO.getDescripcion())
                                .fechaInicio(Instant.now())
                                .build()
                ));

        // Marcar capacitación como COMPLETA
        ProgresoCapacitacionEntity progCap = progCapRepo.findByUsuarioIdAndCapacitacionId(usuarioId, capacitacionId)
                .orElseGet(() -> progCapRepo.save(
                        ProgresoCapacitacionEntity.builder()
                                .usuario(usr)
                                .capacitacion(cap)
                                .estado(EstadoCursosEnum.EN_PROGRESO.getDescripcion())
                                .build()
                ));

        if (!EstadoCursosEnum.COMPLETADO.name().equalsIgnoreCase(progCap.getEstado())) {
            progCap.setEstado(EstadoCursosEnum.COMPLETADO.name());
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
        int capsCompletas = progCapRepo.countByUsuarioIdAndCapacitacion_Curso_IdAndEstado(usuarioId, curso.getId(), EstadoCursosEnum.COMPLETADO.name());

        boolean cursoCompletadoAhora = false;
        boolean insigniaEmitidaAhora = false;

        if (totalCaps > 0 && capsCompletas >= totalCaps) {
            if (!EstadoCursosEnum.COMPLETADO.name().equalsIgnoreCase(progCurso.getEstado())) {
                progCurso.setEstado(EstadoCursosEnum.COMPLETADO.name());
                progCurso.setFechaCompletado(Instant.now());
                progCursoRepo.save(progCurso);
                cursoCompletadoAhora = true;
            }

            // Emitir insignia si no la tiene
            if (!insigniaRepo.existsByUsuarioIdAndCursoId(usuarioId, curso.getId())) {
                InsigniaEntity ins = InsigniaEntity.builder()
                        .usuario(usr)
                        .curso(curso)
                        .nombre(Constantes.COMPLETO_EL_CURSO + curso.getTitulo())
                        .urlImagen(curso.getInsigniaUrl())
                        .build();
                insigniaRepo.save(ins);
                insigniaEmitidaAhora = true;
            }
        }
        enviarNotificacion(curso, usr, cursoCompletadoAhora, insigniaEmitidaAhora);
        return new validarCursoCompleto(cursoCompletadoAhora, insigniaEmitidaAhora);
    }

    private void enviarNotificacion(CursoEntity curso, UsuarioEntity usr, boolean cursoCompletadoAhora, boolean insigniaEmitidaAhora) {
        // Enviar notificación de finalización curso
        if (cursoCompletadoAhora) {
            String urlInsignia = null;
            if (insigniaEmitidaAhora) {
                // Si guardaste la URL en la entidad insignia o curso, úsala:
                // urlInsignia = ins.getUrlImagen();  // Ejemplo
                urlInsignia = (curso.getInsigniaUrl() != null) ? curso.getInsigniaUrl() : null;
            }

            String html = cursoCompletado(
                    usr.getNombre(),                             // nombreUsuario
                    curso.getTitulo(),                           // tituloCurso
                    java.time.format.DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")
                            .withLocale(new java.util.Locale("es", "ES"))
                            .format(java.time.ZonedDateTime.now()), // fechaTexto
                    urlInsignia,                                 // urlInsignia
                    urlInsignia,                                 // enlaceInsignia
                    "http://localhost:4200/login"                  // enlacePortal
            );

            emailService.enviarCorreo(
                    usr.getCorreo(),
                    "¡Has completado el curso " + curso.getTitulo() + "!",
                    html
            );
        }
    }

    private record validarCursoCompleto(boolean cursoCompletadoAhora, boolean insigniaEmitidaAhora) {
    }
}
