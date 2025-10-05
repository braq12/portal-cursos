package com.portal.portal_cursos.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "insignias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsigniaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private CursoEntity curso;

    private String nombre;
    @Column(name = "url_imagen")
    private String urlImagen;

    @Column(name = "fecha_emision", updatable = false)
    private Instant fechaEmision;

    @PrePersist
    void prePersist() {
        if (fechaEmision == null) fechaEmision = Instant.now();
    }
}
