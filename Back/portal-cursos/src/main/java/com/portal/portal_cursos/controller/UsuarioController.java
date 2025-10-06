package com.portal.portal_cursos.controller;



import com.portal.portal_cursos.dtos.usuario.CrearUsuarioRequest;
import com.portal.portal_cursos.dtos.usuario.UsuarioResponse;
import com.portal.portal_cursos.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@RequestBody CrearUsuarioRequest req) {
        return ResponseEntity.ok(usuarioService.crearUsuario(req));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }
}

