package com.portal.portal_cursos.configuracion;

public class NegocioException extends RuntimeException {
    private final String codigo;

    public NegocioException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }
    public String getCodigo() { return codigo; }
}

