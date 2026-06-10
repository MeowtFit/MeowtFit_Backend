package com.meowtfit.backend.carrito.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meowtfit.backend.carrito.dto.CarritoDTO;
import com.meowtfit.backend.carrito.service.CarritoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carritos")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    // Endpoint para crear un nuevo carrito para el usuario autenticado
    @PostMapping
    public ResponseEntity<CarritoDTO> crearCarrito(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carritoService.crearCarritoParaUsuario(authentication.getName()));
    }

    // Endpoint para obtener el carrito activo del usuario autenticado
    @GetMapping("/activo")
    public ResponseEntity<CarritoDTO> obtenerCarritoActivo(Authentication authentication) {
        return ResponseEntity.ok(carritoService.obtenerCarritoActivo(authentication.getName()));
    }

    // Endpoint para listar todos los carritos del usuario autenticado
    @GetMapping
    public ResponseEntity<List<CarritoDTO>> listarCarritos(Authentication authentication) {
        return ResponseEntity.ok(carritoService.listarCarritos(authentication.getName()));
    }

    // Endpoint para desactivar un carrito por su ID
    @PatchMapping("/{idCarrito}/desactivar")
    public ResponseEntity<CarritoDTO> desactivarCarrito(@PathVariable Long idCarrito) {
        return ResponseEntity.ok(carritoService.desactivarCarrito(idCarrito));
    }
}
