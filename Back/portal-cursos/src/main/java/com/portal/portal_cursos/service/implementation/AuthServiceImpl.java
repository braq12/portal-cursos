package com.portal.portal_cursos.service.implementation;


import com.portal.portal_cursos.configuracion.NegocioException;
import com.portal.portal_cursos.dtos.LoginRequest;
import com.portal.portal_cursos.dtos.LoginResponse;
import com.portal.portal_cursos.jpa.entity.UsuarioEntity;
import com.portal.portal_cursos.jpa.repository.UsuarioRepository;
import com.portal.portal_cursos.security.JwtService;
import com.portal.portal_cursos.service.IAuthService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        UsuarioEntity u = usuarios.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new NegocioException("AUTH", "Usuario o contraseña inválidos 1"));

        if (!passwordEncoder.matches(request.getClave(), u.getClaveHash())) {
            throw new NegocioException("AUTH", "Contraseña incorrecta");
        }

        String token = jwtService.generar(
                String.valueOf(u.getId()),
                Map.of("correo", u.getCorreo(), "rol", u.getRol())
        );
        log.info("finaliza login");
        return new LoginResponse(token, u.getRol());
    }
}
