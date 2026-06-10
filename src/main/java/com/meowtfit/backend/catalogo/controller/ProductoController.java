package com.meowtfit.backend.catalogo.controller;

import com.meowtfit.backend.catalogo.dto.ProductoDTO;
import com.meowtfit.backend.catalogo.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.meowtfit.backend.catalogo.dto.ProductoRequestDTO;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<Page<ProductoDTO>> filtrarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) String talla,
            Pageable pageable) {
        
        Page<ProductoDTO> productos = productoService.filtrarProductos(nombre, idCategoria, precioMin, precioMax, talla, pageable);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarProductoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarProductoPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> registrarProducto(@RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.registrarProducto(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> editarProducto(@PathVariable Long id, @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.ok(productoService.editarProducto(id, dto));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ProductoDTO> activarProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.activarProducto(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ProductoDTO> desactivarProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.desactivarProducto(id));
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<ProductoDTO>> listarProductosPorCategoria(@PathVariable Long idCategoria) {
        return ResponseEntity.ok(productoService.listarProductosPorCategoria(idCategoria));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> listarProductosPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.listarProductosPorNombre(nombre));
    }
}
