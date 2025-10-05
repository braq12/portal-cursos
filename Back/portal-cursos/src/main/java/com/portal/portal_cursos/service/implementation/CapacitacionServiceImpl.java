package com.portal.portal_cursos.service.implementation;

import com.portal.portal_cursos.dtos.CapacitacionResponse;
import com.portal.portal_cursos.dtos.CrearCapacitacionUploadRequest;
import com.portal.portal_cursos.jpa.entity.CapacitacionEntity;
import com.portal.portal_cursos.jpa.entity.CursoEntity;
import com.portal.portal_cursos.jpa.repository.CapacitacionRepository;
import com.portal.portal_cursos.jpa.repository.CursoRepository;
import com.portal.portal_cursos.service.IAlmacenamientoService;
import com.portal.portal_cursos.service.ICapacitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
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
        if (!tipo.equals("VIDEO") && !tipo.equals("DOCUMENTO")) {
            throw new IllegalArgumentException("tipo debe ser VIDEO o DOCUMENTO");
        }

        CursoEntity curso = cursoRepository.findById(req.getCursoId())
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        MultipartFile archivo = req.getArchivo();
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("archivo es obligatorio");
        }

        // 2) Definir key en S3 (prefijo por tipo)
        String original = archivo.getOriginalFilename();
        String limpio = sanitizeFilename(original != null ? original : "archivo");
        String extension = obtenerExtension(limpio);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String carpeta = tipo.equals("VIDEO") ? "videos" : "docs";

        // Ej: cursos/<cursoId>/<videos|docs>/<uuid>-<slug>.<ext>
        String baseName = quitarExtension(limpio);
        String slug = aSlug(baseName);
        String key = String.format("cursos/%d/%s/%s-%s.%s",
                curso.getId(), carpeta, uuid, slug, extension);

        // 3) Subir a S3
        long contentLength = archivo.getSize();
        String contentType = archivo.getContentType() != null ? archivo.getContentType() : guessContentType(tipo, extension);

        try (InputStream in = archivo.getInputStream()) {
            storage.subir(key, in, contentLength, contentType);
        } catch (Exception e) {
            throw new RuntimeException("Error subiendo archivo a almacenamiento", e);
        }

        // 4) Persistir capacitación guardando la KEY (no la URL)
        CapacitacionEntity entity = CapacitacionEntity.builder()
                .curso(curso)
                .titulo(req.getTitulo())
                .descripcion(req.getDescripcion())
                .tipo(tipo)
                .keyS3(key) // << guardamos la key S3
                .duracionMinutos(req.getDuracionMinutos())
                .orden(req.getOrden() != null ? req.getOrden() : 0)
                .build();

        CapacitacionEntity guardada = capacitacionRepository.save(entity);

        String urlTemporal= storage.urlTemporal(key,req.getDuracionMinutos()).orElse(null);

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

    // ===== Helpers =====

    private static String sanitizeFilename(String name) {
        // remueve caracteres raros, normaliza acentos, sin espacios raros
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        // quita caracteres no permitidos en S3 keys (dejamos slash para carpetas)
        return normalized.replaceAll("[^A-Za-z0-9._\\- ]", "").trim();
    }

    private static String aSlug(String text) {
        String t = new String(text.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        t = t.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        t = t.replaceAll("(^-|-$)", "");
        return t.isEmpty() ? "archivo" : t;
    }

    private static String obtenerExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return (idx > 0 && idx < filename.length() - 1) ? filename.substring(idx + 1) : "bin";
    }

    private static String quitarExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return (idx > 0) ? filename.substring(0, idx) : filename;
    }

    private static String guessContentType(String tipo, String ext) {
        String e = ext.toLowerCase(Locale.ROOT);
        if (tipo.equals("VIDEO")) {
            if (e.equals("mp4")) return "video/mp4";
            if (e.equals("webm")) return "video/webm";
            if (e.equals("ogg")) return "video/ogg";
            return "application/octet-stream";
        } else {
            if (e.equals("pdf")) return "application/pdf";
            if (e.equals("doc")) return "application/msword";
            if (e.equals("docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            if (e.equals("ppt")) return "application/vnd.ms-powerpoint";
            if (e.equals("pptx")) return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            if (e.equals("txt")) return "text/plain";
            return "application/octet-stream";
        }
    }
}
