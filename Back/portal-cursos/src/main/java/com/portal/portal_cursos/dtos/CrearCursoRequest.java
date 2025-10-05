package com.portal.portal_cursos.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CrearCursoRequest {

    @NotBlank
    private String titulo;
    @NotBlank
    private String descripcion;
    @NotBlank
    private String categoria;

}
