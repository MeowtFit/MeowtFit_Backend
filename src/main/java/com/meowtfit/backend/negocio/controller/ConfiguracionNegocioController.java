package com.meowtfit.backend.negocio.controller;

import com.meowtfit.backend.negocio.dto.ConfiguracionNegocioDTO;
import com.meowtfit.backend.negocio.service.ConfiguracionNegocioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion")
@RequiredArgsConstructor
public class ConfiguracionNegocioController {

    private final ConfiguracionNegocioService service;

    @GetMapping
    public ResponseEntity<ConfiguracionNegocioDTO> obtener() {
        return ResponseEntity.ok(service.obtenerConfiguracion());
    }

    @PostMapping
    public ResponseEntity<ConfiguracionNegocioDTO> guardar(@RequestBody ConfiguracionNegocioDTO dto) {
        return ResponseEntity.ok(service.guardarOModificar(dto));
    }
}
