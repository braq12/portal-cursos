package com.portal.portal_cursos.configuracion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Información del usuario autenticado disponible durante el ciclo de vida del request.
 */
@Component
public class InformacionDeUsuario {

    private static final Logger log = LoggerFactory.getLogger(InformacionDeUsuario.class);


    private final ThreadLocal<Long>   usuarioIdTL = new ThreadLocal<>();
    private final ThreadLocal<String> correoTL    = new ThreadLocal<>();
    private final ThreadLocal<String> rolTL       = new ThreadLocal<>();

    /** Establece los datos del usuario autenticado (lo invoca el filtro JWT). */
    public void establecer(Long usuarioId, String correo, String rol) {
        usuarioIdTL.set(usuarioId);
        correoTL.set(correo);
        rolTL.set(rol);
    }

    /** Limpia SIEMPRE al final del filtro para evitar fugas de memoria en el pool. */
    public void limpiar() {
        usuarioIdTL.remove();
        correoTL.remove();
        rolTL.remove();
    }

    public Long getUsuarioId() { return usuarioIdTL.get(); }
    public String getCorreo()  { return correoTL.get(); }
    public String getRol()     { return rolTL.get(); }

    @Override
    public String toString() {
        return "InformacionDeUsuario{usuarioId=%s, correo=%s, rol=%s}"
                .formatted(getUsuarioId(), getCorreo(), getRol());
    }
}
