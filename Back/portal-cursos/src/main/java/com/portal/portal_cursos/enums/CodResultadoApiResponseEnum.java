package com.portal.portal_cursos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CodResultadoApiResponseEnum {


    OK("Ok"),

    ERROR("Error");

    /** Parámetro codigo */
    private final String codigo;
}
