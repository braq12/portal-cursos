// src/main/java/com/portal/portal_cursos/service/implementation/CursoProgresoServiceImpl.java
package com.portal.portal_cursos.service.implementation;

import com.portal.portal_cursos.dtos.CapacitacionResumenDto;
import com.portal.portal_cursos.dtos.IniciarCursoResponse;
import com.portal.portal_cursos.jpa.entity.CapacitacionEntity;
import com.portal.portal_cursos.jpa.entity.CursoEntity;
import com.portal.portal_cursos.jpa.entity.ProgresoCursoEntity;
import com.portal.portal_cursos.jpa.entity.UsuarioEntity;
import com.portal.portal_cursos.jpa.repository.*;
import com.portal.portal_cursos.service.ICursoProgresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoProgresoServiceImpl implements ICursoProgresoService {

    private final CursoRepository cursoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ProgresoCursoRepository progCursoRepo;
    private final CapacitacionRepository capRepo;
    private final ProgresoCapacitacionRepository progCapRepo;

    @Override
    @Transactional
    public IniciarCursoResponse iniciarCursoYListarCapacitaciones(Long usuarioId, Long cursoId) {
        // 1) Validaciones
        CursoEntity curso = cursoRepo.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        UsuarioEntity usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 2) Idempotencia en progreso_cursos
        var existenteOpt = progCursoRepo.findByUsuarioIdAndCursoId(usuarioId, cursoId);
        boolean yaExistia = existenteOpt.isPresent();
        ProgresoCursoEntity progresoCurso = existenteOpt.orElseGet(() -> {
            ProgresoCursoEntity pc = ProgresoCursoEntity.builder()
                    .usuario(usuario)
                    .curso(curso)
                    .estado("INCOMPLETO")
                    .fechaInicio(Instant.now())
                    .build();
            return progCursoRepo.save(pc);
        });

        // 3) Cargar capacitaciones y progreso (si existiera) para ese usuario
        List<CapacitacionEntity> caps = capRepo.findByCursoIdOrderByOrdenAsc(cursoId);
        List<Long> capIds = caps.stream().map(CapacitacionEntity::getId).toList();

        Map<Long, String> estadoPorCapId = Collections.emptyMap();
        if (!capIds.isEmpty()) {
            estadoPorCapId = progCapRepo.findByUsuarioIdAndCapacitacionIdIn(usuarioId, capIds)
                    .stream()
                    .collect(Collectors.toMap(pc -> pc.getCapacitacion().getId(), pc -> pc.getEstado()));
        }

        List<CapacitacionResumenDto> capsDto = getCapacitacionResumenDtos(caps, estadoPorCapId);

        return IniciarCursoResponse.builder()
                .cursoId(cursoId)
                .yaExistia(yaExistia)
                .estadoCurso(progresoCurso.getEstado()) // "INCOMPLETO" al iniciar
                .totalCapacitaciones(caps.size())
                .capacitaciones(capsDto)
                .build();
    }

    private static List<CapacitacionResumenDto> getCapacitacionResumenDtos(List<CapacitacionEntity> caps, Map<Long, String> estadoPorCapId) {
        List<CapacitacionResumenDto> capsDto = caps.stream()
                .map(c -> {
                    String estado = estadoPorCapId.getOrDefault(c.getId(), "INCOMPLETO");
                    boolean iniciado = estadoPorCapId.containsKey(c.getId());
                    return CapacitacionResumenDto.builder()
                            .id(c.getId())
                            .titulo(c.getTitulo())
                            .descripcion(c.getDescripcion())
                            .tipo(c.getTipo())
                            .duracionMinutos(c.getDuracionMinutos())
                            .orden(c.getOrden())
                            .iniciado(iniciado)
                            .estado(estado)
                            .build();
                }).toList();
        return capsDto;
    }
}
