package com.portal.portal_cursos.utilities;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Slf4j
public class DevPasswordTool {

    private final PasswordEncoder encoder;
    public DevPasswordTool(PasswordEncoder encoder) { this.encoder = encoder; }

    public String hash(String raw) {
        String h = encoder.encode(raw);
        log.info("[DEV] BCrypt('" + raw + "') = " + h);
        return h;
    }
}

