package com.portal.portal_cursos.security;

import com.portal.portal_cursos.configuracion.InformacionDeUsuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final InformacionDeUsuario infoUsuario;

    public JwtAuthenticationFilter(JwtService jwtService, InformacionDeUsuario infoUsuario) {
        this.jwtService = jwtService;
        this.infoUsuario = infoUsuario;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            log.info("Validar headers");
            String auth = request.getHeader("Authorization");
            if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);

                try {
                    Claims claims = jwtService.validarYObtenerClaims(token);

                    String sub = claims.getSubject();
                    String correo = (String) claims.get("correo");
                    String rol    = (String) claims.get("rol");

                    if (sub != null && rol != null) {
                        Long usuarioId = Long.parseLong(sub);
                        var authentication = new UsernamePasswordAuthenticationToken(
                                usuarioId, null, List.of(new SimpleGrantedAuthority(rol))
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        infoUsuario.establecer(usuarioId, correo, rol);
                    }
                } catch (ExpiredJwtException ex) {
                    responder401(response, "token_expirado");
                    return;
                } catch (JwtException | IllegalArgumentException ex) {
                    responder401(response, "token_invalido");
                    return;
                }
            }

            chain.doFilter(request, response);
        } finally {
           // infoUsuario.limpiar();
        }
    }

    private void responder401(HttpServletResponse response, String motivo) throws IOException {
        SecurityContextHolder.clearContext();
        infoUsuario.limpiar();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.setHeader("WWW-Authenticate", "Bearer error=\"" + motivo + "\"");
        response.getWriter().write("{\"error\":\"unauthorized\",\"reason\":\"" + motivo + "\"}");
    }



}
