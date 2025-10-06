package com.portal.portal_cursos.service;

import com.portal.portal_cursos.dtos.usuario.CrearUsuarioRequest;
import com.portal.portal_cursos.dtos.usuario.UsuarioResponse;
import com.portal.portal_cursos.jpa.entity.UsuarioEntity;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    Optional<UsuarioEntity> buscarPorCorreo(String correo);
    public UsuarioResponse crearUsuario(CrearUsuarioRequest req);
    List<UsuarioResponse> listarUsuarios();
}
