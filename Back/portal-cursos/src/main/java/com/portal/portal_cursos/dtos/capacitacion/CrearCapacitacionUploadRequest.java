package com.portal.portal_cursos.dtos.capacitacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearCapacitacionUploadRequest {

    @NotNull
    private Long cursoId;

    @NotBlank
    private String titulo;

    private String descripcion;

    @NotBlank
    private String tipo;

    private Integer duracionMinutos;
    private Integer orden;

    @NotNull
    private MultipartFile archivo;
}
