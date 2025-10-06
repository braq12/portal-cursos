package com.portal.portal_cursos.enums;

public enum EstadoCursosEnum {

    PENDIENTE("PENDIENTE"),
    EN_PROGRESO("EN PROGRESO"),
    COMPLETADO("COMPLETADO");

    private final String descripcion;

    EstadoCursosEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

