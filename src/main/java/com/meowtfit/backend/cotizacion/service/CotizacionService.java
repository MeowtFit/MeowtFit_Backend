package com.meowtfit.backend.cotizacion.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meowtfit.backend.cotizacion.dto.CotizacionDTO;
import com.meowtfit.backend.cotizacion.dto.CotizacionRequestDTO;
import com.meowtfit.backend.cotizacion.dto.ContrapropuestaRequestDTO;
import com.meowtfit.backend.cotizacion.entity.EstadoCotizacion;

public interface CotizacionService {
    
    CotizacionDTO crearCotizacion(CotizacionRequestDTO request, String correoUsuario);
    
    CotizacionDTO obtenerPorId(Long idCotizacion);
    
    Page<CotizacionDTO> filtrarCotizaciones(EstadoCotizacion estado, Long idCliente, Long idComerciante,
            LocalDateTime fechaDesde, LocalDateTime fechaHasta,
            BigDecimal montoMin, BigDecimal montoMax, Pageable pageable);
    
    Page<CotizacionDTO> filtrarMisCotizaciones(String correoUsuario, EstadoCotizacion estado, Pageable pageable);
    
    CotizacionDTO aceptarCotizacion(Long idCotizacion, String correoUsuario);
    
    CotizacionDTO rechazarCotizacion(Long idCotizacion, String correoUsuario);
    
    CotizacionDTO crearContrapropuesta(ContrapropuestaRequestDTO request, String correoUsuario);
}