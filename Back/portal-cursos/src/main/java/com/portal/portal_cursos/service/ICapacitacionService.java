package com.portal.portal_cursos.service;

import com.portal.portal_cursos.dtos.capacitacion.CapacitacionResponse;
import com.portal.portal_cursos.dtos.capacitacion.CrearCapacitacionUploadRequest;

public interface ICapacitacionService {
    CapacitacionResponse crearCapacitacionConArchivo(CrearCapacitacionUploadRequest req);
}
