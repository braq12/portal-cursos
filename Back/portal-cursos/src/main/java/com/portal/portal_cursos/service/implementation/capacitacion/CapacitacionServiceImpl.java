package com.portal.portal_cursos.service.implementation.capacitacion;

import com.portal.portal_cursos.dtos.capacitacion.CapacitacionResponse;
import com.portal.portal_cursos.dtos.capacitacion.CrearCapacitacionUploadRequest;
import com.portal.portal_cursos.enums.CursoTipoEnum;
import com.portal.portal_cursos.jpa.entity.CapacitacionEntity;
import com.portal.portal_cursos.jpa.entity.CursoEntity;
import com.portal.portal_cursos.jpa.repository.CapacitacionRepository;
import com.portal.portal_cursos.jpa.repository.CursoRepository;
import com.portal.portal_cursos.service.IAlmacenamientoService;
import com.portal.portal_cursos.service.ICapacitacionService;
import com.portal.portal_cursos.utilities.Constantes;
import com.portal.portal_cursos.utilities.Utilities;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CapacitacionServiceImpl implements ICapacitacionService {

    private final CapacitacionRepository capacitacionRepository;
    private final CursoRepository cursoRepository;
    private final IAlmacenamientoService storage;

    @Override
    @Transactional
    public CapacitacionResponse crearCapacitacionConArchivo(CrearCapacitacionUploadRequest req) {
        // 1) Validaciones básicas
        String tipo = req.getTipo() == null ? "" : req.getTipo().trim().toUpperCase(Locale.ROOT);
        if (!tipo.equals(CursoTipoEnum.VIDEO.name()) && !tipo.equals(CursoTipoEnum.DOCUMENTO.name())) {
            throw new IllegalArgumentException(Constantes.TIPO_DEBE_SER_VIDEO_O_DOCUMENTO);
        }

        CursoEntity curso = cursoRepository.findById(req.getCursoId())
                .orElseThrow(() -> new IllegalArgumentException(Constantes.CURSO_NO_ENCONTRADO));

        MultipartFile archivo = req.getArchivo();
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException(Constantes.ARCHIVO_ES_OBLIGATORIO);
        }

        // 2) Definir key en S3 (prefijo por tipo)
        String original = archivo.getOriginalFilename();
        String limpio = Utilities.sanitizeFilename(original != null ? original : Constantes.ARCHIVO);
        String extension = Utilities.obtenerExtension(limpio);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String carpeta = tipo.equals(CursoTipoEnum.VIDEO.name()) ? Constantes.VIDEOS : Constantes.DOCS;

        // Ej: cursos/<cursoId>/<videos|docs>/<uuid>-<slug>.<ext>
        String baseName = Utilities.quitarExtension(limpio);
        String slug = Utilities.aSlug(baseName);
        String key = String.format(Constantes.PATRON_KEY_S3,
                curso.getId(), carpeta, uuid, slug, extension);

        // 3) Subir a S3
        long contentLength = archivo.getSize();
        String contentType = archivo.getContentType() != null ? archivo.getContentType() : Utilities.guessContentType(tipo, extension);

        try (InputStream in = archivo.getInputStream()) {
            storage.subir(key, in, contentLength, contentType);
        } catch (Exception e) {
            throw new RuntimeException(Constantes.ERROR_SUBIENDO_ARCHIVO_A_ALMACENAMIENTO, e);
        }

        // 4) Persistir capacitación guardando la KEY
        CapacitacionEntity entity = CapacitacionEntity.builder()
                .curso(curso)
                .titulo(req.getTitulo())
                .descripcion(req.getDescripcion())
                .tipo(tipo)
                .keyS3(key) // << guardar la key S3
                .duracionMinutos(req.getDuracionMinutos())
                .orden(req.getOrden() != null ? req.getOrden() : 0)
                .build();

        CapacitacionEntity guardada = capacitacionRepository.save(entity);

        String urlTemporal = storage.urlTemporal(key, req.getDuracionMinutos()).orElse(null);

        return CapacitacionResponse.builder()
                .id(guardada.getId())
                .titulo(guardada.getTitulo())
                .descripcion(guardada.getDescripcion())
                .tipo(guardada.getTipo())
                .url(urlTemporal)
                .duracionMinutos(guardada.getDuracionMinutos())
                .orden(guardada.getOrden())
                .fechaCreacion(guardada.getFechaCreacion())
                .cursoId(curso.getId())
                .build();
    }


}
