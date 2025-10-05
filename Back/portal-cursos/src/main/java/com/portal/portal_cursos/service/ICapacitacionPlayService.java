package com.portal.portal_cursos.service;

import com.portal.portal_cursos.dtos.IniciarCapacitacionResponse;

public interface ICapacitacionPlayService {

    IniciarCapacitacionResponse iniciarOContinuar(Long usuarioId, Long capacitacionId);

}
