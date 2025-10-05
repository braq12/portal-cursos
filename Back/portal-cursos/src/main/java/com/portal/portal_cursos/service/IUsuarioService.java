package com.portal.portal_cursos.service;

import com.portal.portal_cursos.jpa.entity.UsuarioEntity;
import java.util.Optional;

public interface IUsuarioService {
    Optional<UsuarioEntity> buscarPorCorreo(String correo);
}
