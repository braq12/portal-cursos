package com.portal.portal_cursos.controller;

import com.portal.portal_cursos.configuracion.InformacionDeUsuario;
import com.portal.portal_cursos.dtos.*;
import com.portal.portal_cursos.service.ICursoProgresoService;
import com.portal.portal_cursos.service.ICursoQueryService;
import com.portal.portal_cursos.service.ICursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/cursos",produces= MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CursoController {

    private final ICursoService cursoService;
    private final InformacionDeUsuario infoUsuario;
    private final ICursoQueryService cursoQueryService;
    private final ICursoProgresoService cursoProgresoService;


    @PostMapping
    public ResponseEntity<CursoResponse> crearCurso(@RequestBody @Valid CrearCursoRequest request) {
        Long usuarioId = infoUsuario.getUsuarioId();
        CursoResponse resp = cursoService.crearCurso(usuarioId, request);
        return ResponseEntity.ok(resp);
    }



    @GetMapping("/disponibles")
    public ResponseEntity<List<CursoListadoItemDto>> listarDisponibles() {
        Long usuarioId = infoUsuario.getUsuarioId();
        List<CursoListadoItemDto> data = cursoQueryService.listarCursosDisponiblesParaUsuario(usuarioId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ListarCursosResponse>> listar() {
        List<ListarCursosResponse> data = cursoQueryService.listarCursos();
        return ResponseEntity.ok(data);
    }


    @PostMapping("/{cursoId}/iniciar")
    public ResponseEntity<IniciarCursoResponse> iniciarCurso(
            @PathVariable Long cursoId) {

        Long usuarioId = infoUsuario.getUsuarioId();
        var resp = cursoProgresoService.iniciarCursoYListarCapacitaciones(usuarioId, cursoId);
        return ResponseEntity.ok(resp);
    }
}

