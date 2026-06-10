package com.meowtfit.backend.lineaCarrito.controller;

import java.util.List;

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

import com.meowtfit.backend.lineaCarrito.dto.LineaCarritoDTO;
import com.meowtfit.backend.lineaCarrito.dto.LineaCarritoRequestDTO;
import com.meowtfit.backend.lineaCarrito.service.LineaCarritoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lineas-carrito")
@RequiredArgsConstructor
public class LineaCarritoController {

    private final LineaCarritoService lineaCarritoService;

    @PostMapping
    public ResponseEntity<LineaCarritoDTO> crearLineaCarrito(@Valid @RequestBody LineaCarritoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lineaCarritoService.crearLineaCarrito(dto));
    }

    @PutMapping("/{idLineaCarrito}")
    public ResponseEntity<LineaCarritoDTO> actualizarLineaCarrito(
            @PathVariable Long idLineaCarrito,
            @Valid @RequestBody LineaCarritoRequestDTO dto) {
        return ResponseEntity.ok(lineaCarritoService.actualizarLineaCarrito(idLineaCarrito, dto));
    }

    @DeleteMapping("/{idLineaCarrito}")
    public ResponseEntity<Void> eliminarLineaCarrito(@PathVariable Long idLineaCarrito) {
        lineaCarritoService.eliminarLineaCarrito(idLineaCarrito);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/carrito/{idCarrito}")
    public ResponseEntity<List<LineaCarritoDTO>> listarLineasPorCarrito(@PathVariable Long idCarrito) {
        return ResponseEntity.ok(lineaCarritoService.listarLineasPorCarrito(idCarrito));
    }

    @GetMapping("/variante/{idVariante}")
    public ResponseEntity<List<LineaCarritoDTO>> listarLineasPorVariante(@PathVariable Long idVariante) {
        return ResponseEntity.ok(lineaCarritoService.listarLineasPorVariante(idVariante));
    }
}
