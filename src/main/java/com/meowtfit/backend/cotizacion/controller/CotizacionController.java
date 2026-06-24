package com.meowtfit.backend.cotizacion.controller;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.meowtfit.backend.cotizacion.dto.CotizacionDTO;
import com.meowtfit.backend.cotizacion.dto.CotizacionRequestDTO;
import com.meowtfit.backend.cotizacion.dto.ContrapropuestaRequestDTO;
import com.meowtfit.backend.cotizacion.entity.EstadoCotizacion;
import com.meowtfit.backend.cotizacion.service.CotizacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final CotizacionService cotizacionService;

    // ==================== CREAR COTIZACIONES ====================

    /**
     * Crear cotización manual (solo para clientes B2B)
     * POST /api/cotizaciones
     */
    @PostMapping
    public ResponseEntity<CotizacionDTO> crearCotizacionManual(
            @Valid @RequestBody CotizacionRequestDTO request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cotizacionService.crearCotizacionManual(request, auth.getName()));
    }

    /**
     * Crear cotización automática desde un pedido (cuando supera el límite de 1000 unidades)
     * POST /api/cotizaciones/pedido/{idPedido}
     */
    @PostMapping("/pedido/{idPedido}")
    public ResponseEntity<CotizacionDTO> crearCotizacionDesdePedido(@PathVariable Long idPedido) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cotizacionService.crearCotizacionDesdePedido(idPedido));
    }

    // ==================== OBTENER COTIZACIONES ====================

    /**
     * Obtener cotización por ID
     * GET /api/cotizaciones/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CotizacionDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cotizacionService.obtenerPorId(id));
    }

    /**
     * Listar todas las cotizaciones (solo para ADMIN o COMERCIANTE)
     * GET /api/cotizaciones
     */
    @GetMapping
    public ResponseEntity<List<CotizacionDTO>> listarTodos() {
        return ResponseEntity.ok(cotizacionService.listarTodos());
    }

    /**
     * Listar cotizaciones del usuario autenticado
     * GET /api/cotizaciones/mis-cotizaciones
     */
    @GetMapping("/mis-cotizaciones")
    public ResponseEntity<List<CotizacionDTO>> misCotizaciones(Authentication auth) {
        return ResponseEntity.ok(cotizacionService.listarPorUsuario(auth.getName()));
    }

    /**
     * Listar cotizaciones por estado (solo ADMIN o COMERCIANTE)
     * GET /api/cotizaciones/estado?estado=PENDIENTE
     */
    @GetMapping("/estado")
    public ResponseEntity<List<CotizacionDTO>> listarPorEstado(
            @RequestParam EstadoCotizacion estado) {
        return ResponseEntity.ok(cotizacionService.listarPorEstado(estado));
    }

    // ==================== FILTRADO PAGINADO ====================

    /**
     * Filtrar cotizaciones con paginación (ADMIN o COMERCIANTE)
     * GET /api/cotizaciones/filtrar?estado=PENDIENTE&idUsuario=2&page=0&size=10
     */
    @GetMapping("/filtrar")
    public ResponseEntity<Page<CotizacionDTO>> filtrarCotizaciones(
            @RequestParam(required = false) EstadoCotizacion estado,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) BigDecimal montoMin,
            @RequestParam(required = false) BigDecimal montoMax,
            Pageable pageable) {
        return ResponseEntity.ok(
            cotizacionService.filtrarCotizaciones(estado, idUsuario, fechaDesde, fechaHasta, montoMin, montoMax, pageable)
        );
    }

    /**
     * Filtrar cotizaciones del usuario autenticado con paginación
     * GET /api/cotizaciones/mis-cotizaciones/filtrar?estado=PENDIENTE&page=0&size=5
     */
    @GetMapping("/mis-cotizaciones/filtrar")
    public ResponseEntity<Page<CotizacionDTO>> filtrarMisCotizaciones(
            @RequestParam(required = false) EstadoCotizacion estado,
            Authentication auth,
            Pageable pageable) {
        return ResponseEntity.ok(
            cotizacionService.filtrarMisCotizaciones(auth.getName(), estado, pageable)
        );
    }

    // ==================== GESTIÓN DE ESTADO ====================

    /**
     * Cambiar estado de una cotización
     * PATCH /api/cotizaciones/{id}/estado?nuevoEstado=APROBADA&comentario=Motivo
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<CotizacionDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoCotizacion nuevoEstado,
            @RequestParam(required = false) String comentario) {
        return ResponseEntity.ok(cotizacionService.cambiarEstado(id, nuevoEstado, comentario));
    }

    /**
     * Asignar stock a una cotización (COMERCIANTE)
     * PUT /api/cotizaciones/{id}/asignar-stock
     */
    @PutMapping("/{id}/asignar-stock")
    public ResponseEntity<CotizacionDTO> asignarStock(@PathVariable Long id) {
        return ResponseEntity.ok(cotizacionService.asignarStock(id));
    }

    // ==================== CONTRAPROPUESTAS ====================

    /**
     * Crear una contrapropuesta (cliente o comerciante)
     * POST /api/cotizaciones/contrapropuesta
     */
    @PostMapping("/contrapropuesta")
    public ResponseEntity<CotizacionDTO> crearContrapropuesta(
            @Valid @RequestBody ContrapropuestaRequestDTO request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cotizacionService.crearContrapropuesta(request, auth.getName()));
    }

    /**
     * Aceptar contrapropuesta de una cotización
     * PUT /api/cotizaciones/{id}/aceptar-contrapropuesta
     */
    @PutMapping("/{id}/aceptar-contrapropuesta")
    public ResponseEntity<CotizacionDTO> aceptarContrapropuesta(@PathVariable Long id) {
        return ResponseEntity.ok(cotizacionService.aceptarContrapropuesta(id));
    }

    /**
     * Rechazar contrapropuesta de una cotización
     * PUT /api/cotizaciones/{id}/rechazar-contrapropuesta?motivo=No aceptamos ese precio
     */
    @PutMapping("/{id}/rechazar-contrapropuesta")
    public ResponseEntity<CotizacionDTO> rechazarContrapropuesta(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(cotizacionService.rechazarContrapropuesta(id, motivo));
    }

    /**
     * Contar contrapropuestas de una cotización
     * GET /api/cotizaciones/{id}/contar-contrapropuestas
     */
    @GetMapping("/{id}/contar-contrapropuestas")
    public ResponseEntity<Long> contarContrapropuestas(@PathVariable Long id) {
        return ResponseEntity.ok(cotizacionService.contarContrapropuestas(id));
    }
}