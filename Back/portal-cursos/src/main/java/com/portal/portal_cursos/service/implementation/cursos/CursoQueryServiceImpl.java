package com.portal.portal_cursos.service.implementation.cursos;

import com.portal.portal_cursos.configuracion.InformacionDeUsuario;
import com.portal.portal_cursos.dtos.capacitacion.CapacitacionItemDto;
import com.portal.portal_cursos.dtos.curso.CursoListadoItemDto;
import com.portal.portal_cursos.dtos.curso.ListarCursosResponse;
import com.portal.portal_cursos.enums.EstadoCursosEnum;
import com.portal.portal_cursos.enums.RolesEnum;
import com.portal.portal_cursos.jpa.entity.CapacitacionEntity;
import com.portal.portal_cursos.jpa.entity.CursoEntity;
import com.portal.portal_cursos.jpa.entity.ProgresoCapacitacionEntity;
import com.portal.portal_cursos.jpa.entity.ProgresoCursoEntity;
import com.portal.portal_cursos.jpa.repository.*;
import com.portal.portal_cursos.service.IAlmacenamientoService;
import com.portal.portal_cursos.service.ICursoQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoQueryServiceImpl implements ICursoQueryService {

    private final CursoRepository cursoRepo;
    private final CapacitacionRepository capRepo;
    private final ProgresoCursoRepository progCursoRepo;
    private final ProgresoCapacitacionRepository progCapRepo;
    private final InsigniaRepository insigniaRepo;
    private final InformacionDeUsuario infoUsuario;
    private final IAlmacenamientoService storage;

    @Override
    @Transactional(readOnly = true)
    public List<CursoListadoItemDto> listarCursosDisponiblesParaUsuario(Long usuarioId) {
        // 1) Traer todos los cursos
        List<CursoEntity> cursos = cursoRepo.findAll();
        if (cursos.isEmpty()) return List.of();

        // 2) Pre-cargar progresos de curso e insignias por lote
        List<Long> cursoIds = cursos.stream().map(CursoEntity::getId).toList();

        Map<Long, ProgresoCursoEntity> progCursoPorCursoId = progCursoRepo
                .findByUsuarioIdAndCursoIdIn(usuarioId, cursoIds)
                .stream().collect(Collectors.toMap(p -> p.getCurso().getId(), Function.identity()));

        Set<Long> cursosConInsignia = cursoIds.stream()
                .filter(cid -> insigniaRepo.existsByUsuarioIdAndCursoId(usuarioId, cid))
                .collect(Collectors.toSet());

        // 3) Para cada curso, traer sus capacitaciones y progresos de esas caps
        List<CursoListadoItemDto> resultado = new ArrayList<>(cursos.size());

        procesarCursos(usuarioId, cursos, progCursoPorCursoId, cursosConInsignia, resultado);

        return resultado;
    }

    @Override
    public List<ListarCursosResponse> listarCursos() {
        return cursoRepo.listarCursosOrdenados();
    }

    private void procesarCursos(Long usuarioId, List<CursoEntity> cursos, Map<Long, ProgresoCursoEntity> progCursoPorCursoId, Set<Long> cursosConInsignia, List<CursoListadoItemDto> resultado) {
        for (CursoEntity curso : cursos) {

            capacitacionesProcesadas result = getCapacitacionesProcesadas(usuarioId, curso);

            ProgresoCursoEntity pc = progCursoPorCursoId.get(curso.getId());
            boolean iniciado = (pc != null);
            String estadoCurso = pc != null ? pc.getEstado() : EstadoCursosEnum.PENDIENTE.name();
            boolean tieneInsignia = cursosConInsignia.contains(curso.getId());

            CursoListadoItemDto dto = CursoListadoItemDto.builder()
                    .id(curso.getId())
                    .titulo(curso.getTitulo())
                    .descripcion(curso.getDescripcion())
                    .categoria(curso.getCategoria())
                    .cantidadCapacitaciones(result.cantidadCapacitaciones())
                    .iniciado(iniciado)
                    .estadoCurso(estadoCurso)
                    .tieneInsignia(tieneInsignia)
                    .capacitaciones(result.capsDto())
                    .urlInsignia(curso.getInsigniaUrl())
                    .build();

            resultado.add(dto);
        }
    }

    private capacitacionesProcesadas getCapacitacionesProcesadas(Long usuarioId, CursoEntity curso) {
        List<CapacitacionEntity> caps = capRepo.findByCursoIdOrderByOrdenAsc(curso.getId());
        int cantidadCapacitaciones = caps.size();

        List<Long> capIds = caps.stream().map(CapacitacionEntity::getId).toList();
        Map<Long, String> estadoCapPorId = progCapRepo
                .findByUsuarioIdAndCapacitacionIdIn(usuarioId, capIds)
                .stream()
                .collect(Collectors.toMap(pc -> pc.getCapacitacion().getId(), ProgresoCapacitacionEntity::getEstado));

        List<CapacitacionItemDto> capsDto = caps.stream()
                .map(c -> CapacitacionItemDto.builder()
                        .id(c.getId())
                        .titulo(c.getTitulo())
                        .descripcion(c.getDescripcion())
                        .tipo(c.getTipo())
                        .estado(estadoCapPorId.getOrDefault(c.getId(), EstadoCursosEnum.PENDIENTE.name()))
                        .url(procesarUrlAmin(c))
                        .completada(EstadoCursosEnum.COMPLETADO.name().equalsIgnoreCase(estadoCapPorId.getOrDefault(c.getId(), EstadoCursosEnum.PENDIENTE.name())))
                        .build())
                .toList();
        return new capacitacionesProcesadas(cantidadCapacitaciones, capsDto);
    }

    private String procesarUrlAmin(CapacitacionEntity capacitacion){

        if(RolesEnum.ADMIN.name().equals(infoUsuario.getRol())){
           return storage.urlTemporal(capacitacion.getKeyS3(), capacitacion.getDuracionMinutos()).orElse(null);
        }
        return null;
    }

    private record capacitacionesProcesadas(int cantidadCapacitaciones, List<CapacitacionItemDto> capsDto) {
    }
}

