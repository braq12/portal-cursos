package com.portal.portal_cursos.dtos.capacitacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalizarResponse {
    private Long cursoId;
    private Long capacitacionId;
    private boolean cursoCompletado;
    private boolean insigniaEmitida;
}
