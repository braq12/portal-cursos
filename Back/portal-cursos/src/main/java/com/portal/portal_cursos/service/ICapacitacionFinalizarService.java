package com.portal.portal_cursos.service;

import com.portal.portal_cursos.dtos.capacitacion.FinalizarResponse;

public interface ICapacitacionFinalizarService {
    FinalizarResponse finalizarCapacitacion(Long usuarioId, Long capacitacionId);
}
