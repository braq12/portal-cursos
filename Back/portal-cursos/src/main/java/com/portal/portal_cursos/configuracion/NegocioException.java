package com.portal.portal_cursos.configuracion;

import lombok.Getter;

@Getter
public class NegocioException extends RuntimeException {
    private final String codigo;

    public NegocioException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }
}

