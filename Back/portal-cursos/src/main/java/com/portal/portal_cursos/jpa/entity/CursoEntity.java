package com.portal.portal_cursos.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "cursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private String categoria;

    @ManyToOne
    @JoinColumn(name = "creado_por")
    private UsuarioEntity creadoPor;

    @Column(name = "fecha_creacion", updatable = false)
    private Instant fechaCreacion;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CapacitacionEntity> capacitaciones;

    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) fechaCreacion = Instant.now();
    }
}
