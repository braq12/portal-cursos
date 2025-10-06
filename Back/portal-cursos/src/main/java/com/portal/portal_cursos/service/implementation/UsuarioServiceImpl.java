package com.portal.portal_cursos.service.implementation;

import com.portal.portal_cursos.dtos.usuario.CrearUsuarioRequest;
import com.portal.portal_cursos.dtos.usuario.UsuarioResponse;
import com.portal.portal_cursos.jpa.entity.UsuarioEntity;
import com.portal.portal_cursos.jpa.repository.UsuarioRepository;
import com.portal.portal_cursos.service.IUsuarioService;
import com.portal.portal_cursos.utilities.Constantes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Optional<UsuarioEntity> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    public UsuarioResponse crearUsuario(CrearUsuarioRequest req) {
        if (usuarioRepository.findByCorreo(req.getCorreo()).isPresent()) {
            throw new RuntimeException(Constantes.EL_CORREO_YA_ESTA_REGISTRADO);
        }

        UsuarioEntity nuevo = UsuarioEntity.builder()
                .correo(req.getCorreo())
                .nombre(req.getNombre())
                .rol(req.getRol())
                .claveHash(passwordEncoder.encode(req.getClave()))
                .build();

        UsuarioEntity saved = usuarioRepository.save(nuevo);

        return UsuarioResponse.builder()
                .id(saved.getId())
                .correo(saved.getCorreo())
                .nombre(saved.getNombre())
                .rol(saved.getRol())
                .fechaCreacion(saved.getFechaCreacion())
                .build();
    }

    @Override
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(u -> UsuarioResponse.builder()
                        .id(u.getId())
                        .correo(u.getCorreo())
                        .nombre(u.getNombre())
                        .rol(u.getRol())
                        .fechaCreacion(u.getFechaCreacion())
                        .build())
                .collect(Collectors.toList());
    }
}
