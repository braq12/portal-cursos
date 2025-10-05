package com.portal.portal_cursos.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String correo;
    private String claveHash;
    private String rol;

    @Column(name = "fecha_creacion", updatable = false)
    private Instant fechaCreacion = Instant.now();
}
