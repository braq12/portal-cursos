package com.portal.portal_cursos.service.implementation;

import com.portal.portal_cursos.configuracion.InformacionDeUsuario;
import com.portal.portal_cursos.dtos.CrearCursoRequest;
import com.portal.portal_cursos.dtos.CursoResponse;
import com.portal.portal_cursos.jpa.entity.CursoEntity;
import com.portal.portal_cursos.jpa.entity.UsuarioEntity;
import com.portal.portal_cursos.jpa.repository.CursoRepository;
import com.portal.portal_cursos.jpa.repository.UsuarioRepository;

import com.portal.portal_cursos.service.ICursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements ICursoService{

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InformacionDeUsuario infoUsuario;

    @Override
    @Transactional
    public CursoResponse crearCurso(Long usuarioId, CrearCursoRequest request) {
        UsuarioEntity creador = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        CursoEntity curso = CursoEntity.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .creadoPor(creador)
                .build();

        CursoEntity guardado = cursoRepository.save(curso);

        return new CursoResponse(
                guardado.getId(),
                guardado.getTitulo(),
                guardado.getDescripcion(),
                guardado.getCategoria(),
                guardado.getFechaCreacion()
        );
    }
}
