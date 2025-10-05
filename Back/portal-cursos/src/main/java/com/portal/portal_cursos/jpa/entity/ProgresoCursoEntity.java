package com.portal.portal_cursos.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "progreso_cursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgresoCursoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private CursoEntity curso;

    private String estado;

    @Column(name = "fecha_completado")
    private Instant fechaCompletado;

    @Column(name = "fecha_inicio")
    private Instant fechaInicio;

    @PrePersist
    void prePersist() {
        if (fechaInicio == null) fechaInicio = Instant.now();
    }
}
