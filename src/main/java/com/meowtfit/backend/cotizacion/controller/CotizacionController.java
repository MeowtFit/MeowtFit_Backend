package com.meowtfit.backend.cotizacion.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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

    // ==================== CREAR COTIZACION ====================

    /**
     * Crear cotización manual (solo para clientes B2B)
     * POST /api/cotizaciones
     */
    @PostMapping
    public ResponseEntity<CotizacionDTO> crearCotizacion(
            @Valid @RequestBody CotizacionRequestDTO request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cotizacionService.crearCotizacion(request, auth.getName()));
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

    // ==================== FILTRADO PAGINADO ====================

    /**
     * Filtrar cotizaciones con paginación (ADMIN o COMERCIANTE)
     * GET /api/cotizaciones/filtrar?estado=PENDIENTE&idCliente=2&idComerciante=3&page=0&size=10
     */
    @GetMapping("/filtrar")
    public ResponseEntity<Page<CotizacionDTO>> filtrarCotizaciones(
            @RequestParam(required = false) EstadoCotizacion estado,
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) Long idComerciante,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) BigDecimal montoMin,
            @RequestParam(required = false) BigDecimal montoMax,
            Pageable pageable) {
        return ResponseEntity.ok(
            cotizacionService.filtrarCotizaciones(estado, idCliente, idComerciante, fechaDesde, fechaHasta, montoMin, montoMax, pageable)
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
     * Aceptar cotización (COMERCIANTE o CLIENTE)
     * PUT /api/cotizaciones/{id}/aceptar
     */
    @PutMapping("/{id}/aceptar")
    public ResponseEntity<CotizacionDTO> aceptarCotizacion(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(cotizacionService.aceptarCotizacion(id, auth.getName()));
    }

    /**
     * Rechazar cotización (COMERCIANTE o CLIENTE)
     * PUT /api/cotizaciones/{id}/rechazar
     */
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<CotizacionDTO> rechazarCotizacion(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(cotizacionService.rechazarCotizacion(id, auth.getName()));
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
}