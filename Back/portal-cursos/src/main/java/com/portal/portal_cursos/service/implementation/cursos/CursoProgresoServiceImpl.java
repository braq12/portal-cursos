package com.portal.portal_cursos.service.implementation.cursos;

import com.portal.portal_cursos.dtos.capacitacion.CapacitacionResumenDto;
import com.portal.portal_cursos.dtos.curso.IniciarCursoResponse;
import com.portal.portal_cursos.enums.EstadoCursosEnum;
import com.portal.portal_cursos.jpa.entity.*;
import com.portal.portal_cursos.jpa.repository.*;
import com.portal.portal_cursos.service.ICursoProgresoService;
import com.portal.portal_cursos.utilities.Constantes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
                .orElseThrow(() -> new IllegalArgumentException(Constantes.CURSO_NO_ENCONTRADO));
        UsuarioEntity usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(Constantes.USUARIO_NO_ENCONTRADO));

        // 2) Idempotencia en progreso_cursos
        var existenteOpt = progCursoRepo.findByUsuarioIdAndCursoId(usuarioId, cursoId);
        boolean yaExistia = existenteOpt.isPresent();
        ProgresoCursoEntity progresoCurso = existenteOpt.orElseGet(() -> {
            ProgresoCursoEntity pc = ProgresoCursoEntity.builder()
                    .usuario(usuario)
                    .curso(curso)
                    .estado(EstadoCursosEnum.EN_PROGRESO.getDescripcion())
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
                    .collect(Collectors.toMap(pc -> pc.getCapacitacion().getId(), ProgresoCapacitacionEntity::getEstado));
        }

        List<CapacitacionResumenDto> capsDto = getCapacitacionResumenDtos(caps, estadoPorCapId);

        return IniciarCursoResponse.builder()
                .cursoId(cursoId)
                .yaExistia(yaExistia)
                .estadoCurso(progresoCurso.getEstado())
                .totalCapacitaciones(caps.size())
                .capacitaciones(capsDto)
                .caregoria(curso.getCategoria())
                .titulo(curso.getTitulo())
                .descripcion(curso.getDescripcion())
                .build();
    }

    private static List<CapacitacionResumenDto> getCapacitacionResumenDtos(List<CapacitacionEntity> caps, Map<Long, String> estadoPorCapId) {
        return
                caps.stream()
                        .map(c -> {
                            String estado = estadoPorCapId.getOrDefault(c.getId(), EstadoCursosEnum.PENDIENTE.name());
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
    }
}
