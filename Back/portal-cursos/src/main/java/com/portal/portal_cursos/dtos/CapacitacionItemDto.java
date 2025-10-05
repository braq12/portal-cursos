package com.portal.portal_cursos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacitacionItemDto {
    private Long id;
    private String titulo;
    private String descripcion;
    private String tipo;
    private String estado;
    private boolean completada;
}

