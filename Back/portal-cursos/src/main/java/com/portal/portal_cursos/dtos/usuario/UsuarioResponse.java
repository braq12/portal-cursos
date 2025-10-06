package com.portal.portal_cursos.dtos.usuario;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UsuarioResponse {
    private Long id;
    private String correo;
    private String nombre;
    private String rol;
    private Instant fechaCreacion;
}
