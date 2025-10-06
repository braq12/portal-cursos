package com.portal.portal_cursos.service;

import java.io.InputStream;
import java.util.Optional;

public interface IAlmacenamientoService {

    String subir(String key, InputStream contenido, long contentLength, String contentType);

    String urlPublica(String key);

    Optional<String> urlTemporal(String key, int minutos);

    boolean existe(String key);
}
