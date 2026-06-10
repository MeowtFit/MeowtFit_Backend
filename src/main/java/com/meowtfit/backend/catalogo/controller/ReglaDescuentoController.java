package com.meowtfit.backend.catalogo.controller;

import com.meowtfit.backend.catalogo.dto.ReglaDescuentoDTO;
import com.meowtfit.backend.catalogo.service.ReglaDescuentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reglas")
@RequiredArgsConstructor
public class ReglaDescuentoController {

    private final ReglaDescuentoService service;

    @PostMapping
    public ResponseEntity<ReglaDescuentoDTO> crear(@RequestBody ReglaDescuentoDTO dto) {
        return ResponseEntity.ok(service.crearRegla(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReglaDescuentoDTO> actualizar(@PathVariable Long id, @RequestBody ReglaDescuentoDTO dto) {
        return ResponseEntity.ok(service.actualizarRegla(id, dto));
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<ReglaDescuentoDTO>> listarPorProducto(@PathVariable Long idProducto) {
        return ResponseEntity.ok(service.listarPorProducto(idProducto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarRegla(id);
        return ResponseEntity.noContent().build();
    }
}