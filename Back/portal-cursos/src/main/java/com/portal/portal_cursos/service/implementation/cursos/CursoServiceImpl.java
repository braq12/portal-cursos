package com.portal.portal_cursos.service.implementation.cursos;

import com.portal.portal_cursos.configuracion.NegocioException;
import com.portal.portal_cursos.dtos.curso.CrearCursoRequest;
import com.portal.portal_cursos.dtos.curso.CursoResponse;
import com.portal.portal_cursos.jpa.entity.CursoEntity;
import com.portal.portal_cursos.jpa.entity.UsuarioEntity;
import com.portal.portal_cursos.jpa.repository.CursoRepository;
import com.portal.portal_cursos.jpa.repository.UsuarioRepository;
import com.portal.portal_cursos.service.IAlmacenamientoService;
import com.portal.portal_cursos.service.ICursoService;
import com.portal.portal_cursos.utilities.Constantes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CursoServiceImpl implements ICursoService {

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final IAlmacenamientoService storage;

    private static final Tika TIKA = new Tika();

    // Mimes permitidos (los reales detectados)
    private static final Set<String> ALLOWED_MIMES = Set.of(
            "image/png", "image/jpeg", "image/webp", "image/svg+xml"
    );

    private static final Map<String, String> MIME_TO_EXT = Map.of(
            "image/png", ".png",
            "image/jpeg", ".jpg",
            "image/webp", ".webp",
            "image/svg+xml", ".svg"
    );

    @Override
    @Transactional
    public CursoResponse crearCurso(Long usuarioId, CrearCursoRequest request) {
        UsuarioEntity creador = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(Constantes.USUARIO_NO_ENCONTRADO));

        CursoEntity curso = CursoEntity.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .creadoPor(creador)
                .build();

        CursoEntity guardado = cursoRepository.save(curso);

        if (request.getInsignia() != null && !request.getInsignia().isEmpty()) {
            CargarInsignia(request.getInsignia(), guardado);
        }

        return new CursoResponse(
                guardado.getId(),
                guardado.getTitulo(),
                guardado.getDescripcion(),
                guardado.getCategoria(),
                guardado.getFechaCreacion()
        );
    }

    private void CargarInsignia(MultipartFile insignia, CursoEntity guardado) {
        // 1) leer bytes una vez
        final byte[] bytes;
        try {
            bytes = insignia.getBytes();
        } catch (Exception e) {
            throw new NegocioException("STORAGE", "No fue posible leer la insignia.");
        }

        // 2) detectar MIME real
        String detectedMime;
        try {
            detectedMime = TIKA.detect(bytes, insignia.getOriginalFilename());
        } catch (Exception e) {
            detectedMime = "application/octet-stream";
        }

        log.info("Cliente -> contentType={}, filename={}", insignia.getContentType(), insignia.getOriginalFilename());
        log.info("Detectado -> mime={}, size={}", detectedMime, bytes.length);

        // 3) validar contra MIME real
        if (!ALLOWED_MIMES.contains(detectedMime)) {
            throw new NegocioException("STORAGE",
                    "Formato de imagen inválido. Permitidos: PNG, JPG, WEBP o SVG.");
        }

        // 4) decidir extensión a partir del MIME real
        String ext = MIME_TO_EXT.getOrDefault(detectedMime, "");

        // 5) construir key y subir con el MIME detectado
        String key = "cursos/%d/insignia/insignia%s".formatted(guardado.getId(), ext);

        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            key = storage.subir(
                    key,
                    in,
                    bytes.length,
                    detectedMime   // usa el MIME detectado
            );
            guardado.setInsigniaKey(key);
            guardado.setInsigniaUrl(storage.urlPublica(key));
            cursoRepository.save(guardado);
        } catch (Exception e) {
            log.error("Error cargando archivo a S3", e);
            throw new NegocioException("STORAGE", "No fue posible almacenar la insignia.");
        }
    }
}
