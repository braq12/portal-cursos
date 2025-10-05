package com.portal.portal_cursos.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "capacitaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacitacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private CursoEntity curso;

    private String titulo;
    private String descripcion;
    private String tipo;

    @Column(name="KEY_S3")
    private String keyS3;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    private Integer orden;

    @Column(name = "fecha_creacion", updatable = false)
    private Instant fechaCreacion;

    @OneToMany(mappedBy = "capacitacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgresoCapacitacionEntity> progresos;

    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) fechaCreacion = Instant.now();
    }
}
