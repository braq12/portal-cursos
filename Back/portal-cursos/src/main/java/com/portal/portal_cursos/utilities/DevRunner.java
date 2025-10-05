package com.portal.portal_cursos.utilities;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevRunner implements CommandLineRunner {

    private final DevPasswordTool tool;
    public DevRunner(DevPasswordTool tool) { this.tool = tool; }

    @Override
    public void run(String... args) {
        tool.hash("clave123");
    }
}
