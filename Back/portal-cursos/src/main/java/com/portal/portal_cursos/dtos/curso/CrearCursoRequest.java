package com.portal.portal_cursos.dtos.curso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CrearCursoRequest {

    @NotBlank
    private String titulo;
    @NotBlank
    private String descripcion;
    @NotBlank
    private String categoria;

    @NotNull
    private MultipartFile insignia;

}
