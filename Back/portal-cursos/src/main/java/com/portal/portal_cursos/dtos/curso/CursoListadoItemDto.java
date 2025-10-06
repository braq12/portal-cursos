package com.portal.portal_cursos.dtos.curso;

import com.portal.portal_cursos.dtos.capacitacion.CapacitacionItemDto;
import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CursoListadoItemDto {
    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private int cantidadCapacitaciones;
    private boolean iniciado;
    private String estadoCurso;
    private boolean tieneInsignia;
    private String urlInsignia;

    private List<CapacitacionItemDto> capacitaciones;
}
