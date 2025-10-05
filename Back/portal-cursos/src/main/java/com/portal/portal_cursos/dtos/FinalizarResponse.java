package com.portal.portal_cursos.dtos;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FinalizarResponse {
    private Long cursoId;
    private Long capacitacionId;
    private boolean cursoCompletado;
    private boolean insigniaEmitida;
}
