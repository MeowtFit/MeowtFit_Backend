package com.meowtfit.backend.catalogo.controller;

import com.meowtfit.backend.catalogo.service.VarianteProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/variantes")
@RequiredArgsConstructor
public class VarianteProductoController {

    private final VarianteProductoService varianteProductoService;

    // Métodos para variantes si fueran necesarios en el futuro
}
