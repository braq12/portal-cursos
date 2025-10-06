package com.portal.portal_cursos.dtos.capacitacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacitacionResumenDto {
    private Long id;
    private String titulo;
    private String descripcion;
    private String tipo;
    private Integer duracionMinutos;
    private Integer orden;
    private boolean iniciado;
    private String estado;
}
