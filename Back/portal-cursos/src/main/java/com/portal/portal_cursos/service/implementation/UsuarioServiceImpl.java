package com.portal.portal_cursos.service.implementation;

import com.portal.portal_cursos.jpa.entity.UsuarioEntity;
import com.portal.portal_cursos.jpa.repository.UsuarioRepository;
import com.portal.portal_cursos.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {
    private final UsuarioRepository usuarioRepository;
    @Override public Optional<UsuarioEntity> buscarPorCorreo(String correo){
        return usuarioRepository.findByCorreo(correo);
    }
}
