package com.portal.portal_cursos.dtos.curso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListarCursosResponse {
    private Long idCurso;
    private String titulo;
    private String descripcion;

}
