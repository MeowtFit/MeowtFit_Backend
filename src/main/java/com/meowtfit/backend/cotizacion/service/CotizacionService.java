package com.meowtfit.backend.cotizacion.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meowtfit.backend.cotizacion.dto.CotizacionDTO;
import com.meowtfit.backend.cotizacion.dto.CotizacionRequestDTO;
import com.meowtfit.backend.cotizacion.dto.ContrapropuestaRequestDTO;
import com.meowtfit.backend.cotizacion.entity.EstadoCotizacion;

public interface CotizacionService {
    
    // Crear cotización desde pedido (cuando supera el límite de 1000 unidades)
    CotizacionDTO crearCotizacionDesdePedido(Long idPedido);
    
    // Crear cotización manual (cliente B2B solicita)
    CotizacionDTO crearCotizacionManual(CotizacionRequestDTO request, String correoUsuario);
    
    // Obtener cotización por ID
    CotizacionDTO obtenerPorId(Long idCotizacion);
    
    // Listar cotizaciones por usuario
    List<CotizacionDTO> listarPorUsuario(String correoUsuario);
    
    // Listar cotizaciones por estado
    List<CotizacionDTO> listarPorEstado(EstadoCotizacion estado);
    
    // Listar todas (para admin)
    List<CotizacionDTO> listarTodos();
    
    // Cambiar estado de cotización
    CotizacionDTO cambiarEstado(Long idCotizacion, EstadoCotizacion nuevoEstado, String comentario);
    
    // Asignar stock a una cotización (comerciante)
    CotizacionDTO asignarStock(Long idCotizacion);
    
    // Crear contrapropuesta (cliente o comerciante)
    CotizacionDTO crearContrapropuesta(ContrapropuestaRequestDTO request, String correoUsuario);
    
    // Aceptar contrapropuesta
    CotizacionDTO aceptarContrapropuesta(Long idCotizacion);
    
    // Rechazar contrapropuesta
    CotizacionDTO rechazarContrapropuesta(Long idCotizacion, String motivo);
    
    // Filtrar cotizaciones paginadas
    Page<CotizacionDTO> filtrarCotizaciones(EstadoCotizacion estado, Long idUsuario,
            LocalDateTime fechaDesde, LocalDateTime fechaHasta,
            BigDecimal montoMin, BigDecimal montoMax, Pageable pageable);
    
    // Filtrar cotizaciones de un usuario específico
    Page<CotizacionDTO> filtrarMisCotizaciones(String correoUsuario, EstadoCotizacion estado, Pageable pageable);
    
    // Contar contrapropuestas de una cotización (para validar límite de 5)
    long contarContrapropuestas(Long idCotizacion);
}