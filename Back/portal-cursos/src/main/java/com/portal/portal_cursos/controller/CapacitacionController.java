package com.portal.portal_cursos.controller;

import com.portal.portal_cursos.configuracion.InformacionDeUsuario;
import com.portal.portal_cursos.dtos.CapacitacionResponse;
import com.portal.portal_cursos.dtos.CrearCapacitacionUploadRequest;
import com.portal.portal_cursos.dtos.FinalizarResponse;
import com.portal.portal_cursos.dtos.IniciarCapacitacionResponse;
import com.portal.portal_cursos.service.ICapacitacionPlayService;
import com.portal.portal_cursos.service.ICapacitacionProgresoService;
import com.portal.portal_cursos.service.ICapacitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/capacitaciones")
@RequiredArgsConstructor
@Slf4j
public class CapacitacionController {

    private final ICapacitacionService capacitacionService;

    private final ICapacitacionPlayService capacitacionPlayService;
    private final InformacionDeUsuario infoUsuario;

    private final ICapacitacionProgresoService capacitacionProgresoService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CapacitacionResponse> crearCapacitacionUpload(
            @Valid @ModelAttribute CrearCapacitacionUploadRequest request) {
        log.info("Inicio cargue de capacitacion");
        CapacitacionResponse resp = capacitacionService.crearCapacitacionConArchivo(request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{capacitacionId}/iniciar")
    public ResponseEntity<IniciarCapacitacionResponse> iniciarOContinuar(
            @PathVariable Long capacitacionId) {

        Long usuarioId = infoUsuario.getUsuarioId();
        var resp = capacitacionPlayService.iniciarOContinuar(usuarioId, capacitacionId);
        return ResponseEntity.ok(resp);
    }


    @PostMapping("/{capacitacionId}/finalizar")
    public ResponseEntity<FinalizarResponse> finalizar(@PathVariable Long capacitacionId) {
        Long usuarioId = infoUsuario.getUsuarioId();
        return ResponseEntity.ok(capacitacionProgresoService.finalizarCapacitacion(usuarioId, capacitacionId));
    }
}
