-- =====================================================================================
-- Base de datos
-- =====================================================================================
CREATE DATABASE IF NOT EXISTS portal_cursos
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE portal_cursos;

-- =====================================================================================
-- 1) Tabla de usuarios
-- =====================================================================================
CREATE TABLE IF NOT EXISTS usuarios (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  correo           VARCHAR(255) NOT NULL UNIQUE,
  clave_hash       VARCHAR(255) NOT NULL,
  rol              VARCHAR(20) NOT NULL,
  nombre		   VARCHAR(225) NOT NULL,	
  fecha_creacion   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =====================================================================================
-- 2) Tabla de cursos (nivel principal)
-- =====================================================================================
CREATE TABLE IF NOT EXISTS cursos (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  titulo           VARCHAR(255) NOT NULL,
  descripcion      TEXT,
  categoria        VARCHAR(40),
  creado_por       BIGINT,
  fecha_creacion   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  insignia_key     VARCHAR(512) NULL,
  insignia_url     VARCHAR(1024) NULL,
  CONSTRAINT fk_cursos_creado_por FOREIGN KEY (creado_por) REFERENCES usuarios(id)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- =====================================================================================
-- 3) Tabla de capacitaciones (módulos dentro de un curso)
-- =====================================================================================
CREATE TABLE IF NOT EXISTS capacitaciones (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  curso_id         BIGINT NOT NULL,
  titulo           VARCHAR(255) NOT NULL,
  descripcion      TEXT,
  tipo             VARCHAR(20) NOT NULL,  -- VIDEO o DOCUMENTO
  key_s3              TEXT NOT NULL,         -- puede ser S3 key
  duracion_minutos INT,
  orden            INT DEFAULT 0,         -- para el orden de los módulos
  fecha_creacion   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_capacitacion_curso FOREIGN KEY (curso_id) REFERENCES cursos(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- =====================================================================================
-- 4) Progreso de capacitaciones
-- =====================================================================================
CREATE TABLE IF NOT EXISTS progreso_capacitaciones (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id       BIGINT NOT NULL,
  capacitacion_id  BIGINT NOT NULL,
  estado           varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  fecha_inicio     TIMESTAMP NOT NULL,
  fecha_completado TIMESTAMP NULL,
  CONSTRAINT fk_pc_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_pc_capacitacion FOREIGN KEY (capacitacion_id) REFERENCES capacitaciones(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT uq_pc_usuario_capacitacion UNIQUE (usuario_id, capacitacion_id),
  CONSTRAINT chk_pc_estado_cap CHECK (estado IN ('PENDIENTE','EN PROGRESO','COMPLETADO'))
) ENGINE=InnoDB;

-- =====================================================================================
-- 5) Progreso de cursos (acumulado a nivel de curso)
-- =====================================================================================
CREATE TABLE IF NOT EXISTS progreso_cursos (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id       BIGINT NOT NULL,
  curso_id         BIGINT NOT NULL,
  estado           varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  fecha_completado TIMESTAMP NULL,
  fecha_inicio     TIMESTAMP NOT NULL,
  CONSTRAINT fk_progcurso_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_progcurso_curso FOREIGN KEY (curso_id) REFERENCES cursos(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT uq_progcurso_usuario_curso UNIQUE (usuario_id, curso_id),
  CONSTRAINT chk_pc_estado CHECK (estado IN ('PENDIENTE','EN PROGRESO','COMPLETADO'))
) ENGINE=InnoDB;

-- =====================================================================================
-- 6) Insignias
-- =====================================================================================
CREATE TABLE IF NOT EXISTS insignias (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id    BIGINT NOT NULL,
  curso_id      BIGINT NOT NULL,
  nombre        VARCHAR(255) NOT NULL,
  url_imagen    TEXT,
  fecha_emision TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ins_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_ins_curso FOREIGN KEY (curso_id) REFERENCES cursos(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT uq_ins_usuario_curso UNIQUE (usuario_id, curso_id)
) ENGINE=InnoDB;

-- =====================================================================================
-- 7) Vista reportes
-- =====================================================================================

DROP VIEW IF EXISTS vw_reporte_usuario_cursos;

CREATE VIEW vw_reporte_usuario_cursos AS
SELECT
    u.id                               AS usuario_id,
    u.nombre                           AS usuario,
    SUM(pc.estado = 'COMPLETADO')      AS completados,
    SUM(pc.estado = 'EN PROGRESO')     AS en_progreso,
    SUM(pc.estado = 'PENDIENTE')       AS pendientes,

    COALESCE(GROUP_CONCAT(IF(pc.estado='COMPLETADO', c.titulo, NULL)
             ORDER BY c.titulo SEPARATOR ' | '), '') AS cursos_completados,

    COALESCE(GROUP_CONCAT(IF(pc.estado='EN PROGRESO', c.titulo, NULL)
             ORDER BY c.titulo SEPARATOR ' | '), '') AS cursos_en_progreso,

    COALESCE(GROUP_CONCAT(IF(pc.estado='PENDIENTE', c.titulo, NULL)
             ORDER BY c.titulo SEPARATOR ' | '), '') AS cursos_pendientes
FROM usuarios u
LEFT JOIN progreso_cursos pc ON pc.usuario_id = u.id
LEFT JOIN cursos c           ON c.id = pc.curso_id
where u.rol <>'ADMIN'
GROUP BY u.id, u.nombre
ORDER BY u.nombre;

DROP VIEW IF EXISTS vw_reporte_usuario_curso_detalle;

CREATE VIEW vw_reporte_usuario_curso_detalle AS
SELECT
  u.id     AS usuario_id,
  u.nombre AS usuario,
  c.id     AS curso_id,
  c.titulo AS curso,
  pc.estado,
  pc.fecha_inicio,
  pc.fecha_completado
FROM progreso_cursos pc
JOIN usuarios u ON u.id = pc.usuario_id
JOIN cursos   c ON c.id = pc.curso_id;


CREATE INDEX idx_progcurso_usuario   ON progreso_cursos (usuario_id);
CREATE INDEX idx_progcurso_estado    ON progreso_cursos (estado);
CREATE INDEX idx_progcurso_curso     ON progreso_cursos (curso_id);


-- =====================================================================================
-- 8) Datos iniciales
-- =====================================================================================
INSERT INTO usuarios (correo, clave_hash, rol,nombre)
VALUES
  ('ADMIN@ADMIN.com', '$2a$10$tMWjwf1964bj1JrkDPkyPeRD36w/43VJ2FGXWIG.l/cJ3aApehkV2', 'ADMIN','Brhayan Roa'),
  ('brayanrao@live.com', '$2a$10$tMWjwf1964bj1JrkDPkyPeRD36w/43VJ2FGXWIG.l/cJ3aApehkV2', 'USUARIO','Carmelo lopez')
ON DUPLICATE KEY UPDATE correo = VALUES(correo);


COMMIT;
