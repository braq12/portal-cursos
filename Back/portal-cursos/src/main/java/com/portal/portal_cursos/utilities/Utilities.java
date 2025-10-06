package com.portal.portal_cursos.utilities;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;

@UtilityClass
public class Utilities {
    public static String sanitizeFilename(String name) {
        // remueve caracteres raros, normaliza acentos, sin espacios raros
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        // quita caracteres no permitidos en S3 keys
        return normalized.replaceAll("[^A-Za-z0-9._\\- ]", "").trim();
    }

    public static String aSlug(String text) {
        String t = new String(text.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        t = t.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        t = t.replaceAll("(^-|-$)", "");
        return t.isEmpty() ? Constantes.ARCHIVO : t;
    }

    public static String obtenerExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return (idx > 0 && idx < filename.length() - 1) ? filename.substring(idx + 1) : "bin";
    }

    public static String quitarExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return (idx > 0) ? filename.substring(0, idx) : filename;
    }

    public static String guessContentType(String tipo, String ext) {
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
