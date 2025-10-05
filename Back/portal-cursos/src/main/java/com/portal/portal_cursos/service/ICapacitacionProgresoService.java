package com.portal.portal_cursos.service;

import com.portal.portal_cursos.dtos.FinalizarResponse;

public interface ICapacitacionProgresoService {
    FinalizarResponse finalizarCapacitacion(Long usuarioId, Long capacitacionId);
}
