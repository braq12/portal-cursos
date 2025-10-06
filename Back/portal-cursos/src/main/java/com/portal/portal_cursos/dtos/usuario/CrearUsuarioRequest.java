package com.portal.portal_cursos.dtos.usuario;

import lombok.Data;

@Data
public class CrearUsuarioRequest {
    private String correo;
    private String nombre;
    private String clave;
    private String rol;
}
