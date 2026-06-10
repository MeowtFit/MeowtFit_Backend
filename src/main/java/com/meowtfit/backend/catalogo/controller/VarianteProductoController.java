package com.meowtfit.backend.catalogo.controller;

import com.meowtfit.backend.catalogo.service.VarianteProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meowtfit.backend.catalogo.dto.VarianteProductoDTO;
import com.meowtfit.backend.catalogo.dto.VarianteProductoRequestDTO;

import java.util.List;

@RestController
@RequestMapping("/api/variantes")
@RequiredArgsConstructor
public class VarianteProductoController {

    private final VarianteProductoService varianteProductoService;

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<VarianteProductoDTO>> obtenerVariantesPorProducto(@PathVariable Long idProducto) {
        return ResponseEntity.ok(varianteProductoService.obtenerVariantesPorProducto(idProducto));
    }

    @PostMapping
    public ResponseEntity<VarianteProductoDTO> registrarVarianteProducto(@RequestBody VarianteProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(varianteProductoService.registrarVarianteProducto(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VarianteProductoDTO> editarVarianteProducto(@PathVariable Long id, @RequestBody VarianteProductoRequestDTO dto) {
        return ResponseEntity.ok(varianteProductoService.editarVarianteProducto(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVarianteProducto(@PathVariable Long id) {
        varianteProductoService.eliminarVarianteProducto(id);
        return ResponseEntity.noContent().build();
    }
}
