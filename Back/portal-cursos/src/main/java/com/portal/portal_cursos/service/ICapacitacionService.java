package com.portal.portal_cursos.service;

import com.portal.portal_cursos.dtos.CapacitacionResponse;
import com.portal.portal_cursos.dtos.CrearCapacitacionUploadRequest;

public interface ICapacitacionService {
    CapacitacionResponse crearCapacitacionConArchivo(CrearCapacitacionUploadRequest req);
}
