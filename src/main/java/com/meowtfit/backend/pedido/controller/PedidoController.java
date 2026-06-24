package com.meowtfit.backend.pedido.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.meowtfit.backend.pedido.dto.PedidoDTO;
import com.meowtfit.backend.pedido.dto.PedidoRequestDTO;
import com.meowtfit.backend.pedido.entity.EstadoPedido;
import com.meowtfit.backend.pedido.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // Crear un pedido para el usuario logueado
    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(@Valid @RequestBody PedidoRequestDTO dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.crearPedido(dto, auth.getName()));
    }

    // Historial de pedidos del usuario logueado
    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoDTO>> misPedidos(Authentication auth) {
        return ResponseEntity.ok(pedidoService.listarPorUsuario(auth.getName()));
    }

    // Obtener un pedido específico (podrías añadir validación para que solo el dueño o el admin lo vea)
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    // Solo para ADMINISTRADOR o COMERCIANTE
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    // Cambiar el estado del pedido (ej. de REGISTRADO a ENVIADO)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoDTO> cambiarEstado(
            @PathVariable Long id, 
            @RequestParam EstadoPedido nuevoEstado,
            @RequestParam(required = false) String motivoRechazo) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, nuevoEstado, motivoRechazo));
    }

    // Filtrar todos los pedidos con paginación (admin/comerciante)
    // Ejemplo: GET /api/pedidos/filtrar?estado=REGISTRADO&idUsuario=2&page=0&size=10
    @GetMapping("/filtrar")
    public ResponseEntity<Page<PedidoDTO>> filtrarPedidos(
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) BigDecimal montoMin,
            @RequestParam(required = false) BigDecimal montoMax,
            Pageable pageable) {
        return ResponseEntity.ok(
            pedidoService.filtrarPedidos(estado, idUsuario, fechaDesde, fechaHasta, montoMin, montoMax, pageable)
        );
    }

    // Filtrar los pedidos del usuario logueado con paginación (cliente)
    // Ejemplo: GET /api/pedidos/mis-pedidos/filtrar?estado=ENVIADO&page=0&size=5
    @GetMapping("/mis-pedidos/filtrar")
    public ResponseEntity<Page<PedidoDTO>> filtrarMisPedidos(
            @RequestParam(required = false) EstadoPedido estado,
            Authentication auth,
            Pageable pageable) {
        return ResponseEntity.ok(pedidoService.filtrarMisPedidos(auth.getName(), estado, pageable));
    }
}