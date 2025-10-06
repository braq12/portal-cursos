package com.portal.portal_cursos.service.implementacion.capacitacion;


import com.portal.portal_cursos.dtos.capacitacion.FinalizarResponse;
import com.portal.portal_cursos.enums.EstadoCursosEnum;
import com.portal.portal_cursos.jpa.entity.*;
import com.portal.portal_cursos.jpa.repository.*;
import com.portal.portal_cursos.service.IEmailService;
import com.portal.portal_cursos.service.implementation.capacitacion.CapacitacionFinalizarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapacitacionFinalizarServiceImplTest {

    @Mock private CapacitacionRepository capRepo;
    @Mock private UsuarioRepository usuarioRepo;
    @Mock private ProgresoCapacitacionRepository progCapRepo;
    @Mock private ProgresoCursoRepository progCursoRepo;
    @Mock private InsigniaRepository insigniaRepo;
    @Mock private IEmailService emailService;

    @InjectMocks
    private CapacitacionFinalizarServiceImpl service;

    private UsuarioEntity usr;
    private CursoEntity curso;
    private CapacitacionEntity cap;

    private static final Long USUARIO_ID = 10L;
    private static final Long CURSO_ID = 20L;
    private static final Long CAP_ID = 30L;

    @BeforeEach
    void setUp() {
        usr = new UsuarioEntity();
        usr.setId(USUARIO_ID);
        usr.setCorreo("test@correo.com");
        usr.setNombre("Tester");

        curso = new CursoEntity();
        curso.setId(CURSO_ID);
        curso.setTitulo("Curso Angular");
        // si tu entidad tiene setInsigniaUrl
        curso.setInsigniaUrl("https://cdn.example.com/insignias/angular.png");

        cap = new CapacitacionEntity();
        cap.setId(CAP_ID);
        cap.setCurso(curso);
    }

    @Test
    void debe_marcar_capacitacion_completada_y_no_enviar_mail_si_no_es_ultima() {
        // Arrange
        when(capRepo.findById(CAP_ID)).thenReturn(Optional.of(cap));
        when(usuarioRepo.findById(USUARIO_ID)).thenReturn(Optional.of(usr));

        // progreso del curso NO existe -> se crea EN_PROGRESO
        when(progCursoRepo.findByUsuarioIdAndCursoId(USUARIO_ID, CURSO_ID))
                .thenReturn(Optional.empty());
        when(progCursoRepo.save(any(ProgresoCursoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // progreso de la capacitación NO existe -> se crea EN_PROGRESO y luego se completa
        when(progCapRepo.findByUsuarioIdAndCapacitacionId(USUARIO_ID, CAP_ID))
                .thenReturn(Optional.empty());
        when(progCapRepo.save(any(ProgresoCapacitacionEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // El curso tiene 3 caps y SOLO hay 2 completas después de esta -> no es la última
        when(capRepo.countByCursoId(CURSO_ID)).thenReturn(3);
        when(progCapRepo.countByUsuarioIdAndCapacitacion_Curso_IdAndEstado(
                eq(USUARIO_ID), eq(CURSO_ID), eq(EstadoCursosEnum.COMPLETADO.name())
        )).thenReturn(2);

        // Act
        FinalizarResponse resp = service.finalizarCapacitacion(USUARIO_ID, CAP_ID);

        // Assert
        assertThat(resp.getCursoId()).isEqualTo(CURSO_ID);
        assertThat(resp.getCapacitacionId()).isEqualTo(CAP_ID);
        assertThat(resp.isCursoCompletado()).isFalse();
        assertThat(resp.isInsigniaEmitida()).isFalse();

        // verificaciones: se guardó progreso de cap completado
        verify(progCapRepo, atLeastOnce()).save(argThat(pc ->
                pc.getCapacitacion().getId().equals(CAP_ID) &&
                        EstadoCursosEnum.COMPLETADO.name().equalsIgnoreCase(pc.getEstado()) &&
                        pc.getFechaCompletado() != null
        ));

        // NO se marcó curso como COMPLETADO ni se envió correo
        verify(progCursoRepo, never()).save(argThat(pc ->
                EstadoCursosEnum.COMPLETADO.name().equalsIgnoreCase(pc.getEstado())
        ));
        verify(emailService, never()).enviarCorreo(anyString(), anyString(), anyString());
        verify(insigniaRepo, never()).save(any(InsigniaEntity.class));
    }

    @Test
    void debe_completar_curso_emitir_insignia_y_enviar_correo_si_es_ultima() {
        // Arrange
        when(capRepo.findById(CAP_ID)).thenReturn(Optional.of(cap));
        when(usuarioRepo.findById(USUARIO_ID)).thenReturn(Optional.of(usr));

        // progreso curso YA existe en EN_PROGRESO
        ProgresoCursoEntity progCursoExistente = new ProgresoCursoEntity();
        progCursoExistente.setId(100L);
        progCursoExistente.setUsuario(usr);
        progCursoExistente.setCurso(curso);
        progCursoExistente.setEstado(EstadoCursosEnum.EN_PROGRESO.getDescripcion());
        progCursoExistente.setFechaInicio(Instant.now().minusSeconds(3600));
        when(progCursoRepo.findByUsuarioIdAndCursoId(USUARIO_ID, CURSO_ID))
                .thenReturn(Optional.of(progCursoExistente));
        when(progCursoRepo.save(any(ProgresoCursoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // progreso capacitacion YA existe en EN_PROGRESO y será completado
        ProgresoCapacitacionEntity progCapExistente = new ProgresoCapacitacionEntity();
        progCapExistente.setId(200L);
        progCapExistente.setUsuario(usr);
        progCapExistente.setCapacitacion(cap);
        progCapExistente.setEstado(EstadoCursosEnum.EN_PROGRESO.getDescripcion());
        when(progCapRepo.findByUsuarioIdAndCapacitacionId(USUARIO_ID, CAP_ID))
                .thenReturn(Optional.of(progCapExistente));
        when(progCapRepo.save(any(ProgresoCapacitacionEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // Es la ÚLTIMA: total 3 y completas 3 tras guardar
        when(capRepo.countByCursoId(CURSO_ID)).thenReturn(3);
        when(progCapRepo.countByUsuarioIdAndCapacitacion_Curso_IdAndEstado(
                eq(USUARIO_ID), eq(CURSO_ID), eq(EstadoCursosEnum.COMPLETADO.name())
        )).thenReturn(3);

        // insignia NO existe aún
        when(insigniaRepo.existsByUsuarioIdAndCursoId(USUARIO_ID, CURSO_ID)).thenReturn(false);
        when(insigniaRepo.save(any(InsigniaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        FinalizarResponse resp = service.finalizarCapacitacion(USUARIO_ID, CAP_ID);

        // Assert
        assertThat(resp.isCursoCompletado()).isTrue();
        assertThat(resp.isInsigniaEmitida()).isTrue();

        // Curso marcado como COMPLETADO
        verify(progCursoRepo).save(argThat(pc ->
                pc.getCurso().getId().equals(CURSO_ID) &&
                        EstadoCursosEnum.COMPLETADO.name().equalsIgnoreCase(pc.getEstado()) &&
                        pc.getFechaCompletado() != null
        ));

        // Insignia emitida con url del curso
        verify(insigniaRepo).save(argThat(ins ->
                ins.getCurso().getId().equals(CURSO_ID) &&
                        ins.getUsuario().getId().equals(USUARIO_ID) &&
                        ins.getUrlImagen().equals(curso.getInsigniaUrl())
        ));

        // Correo enviado
        verify(emailService, times(1))
                .enviarCorreo(eq("test@correo.com"), contains("Has completado el curso"), anyString());
    }

    @Test
    void debe_lanzar_excepcion_si_no_existe_capacitacion() {
        when(capRepo.findById(CAP_ID)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.finalizarCapacitacion(USUARIO_ID, CAP_ID));
        verifyNoInteractions(usuarioRepo, progCapRepo, progCursoRepo, insigniaRepo, emailService);
    }

    @Test
    void debe_lanzar_excepcion_si_no_existe_usuario() {
        when(capRepo.findById(CAP_ID)).thenReturn(Optional.of(cap));
        when(usuarioRepo.findById(USUARIO_ID)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.finalizarCapacitacion(USUARIO_ID, CAP_ID));
        verifyNoInteractions(progCapRepo, progCursoRepo, insigniaRepo, emailService);
    }
}

