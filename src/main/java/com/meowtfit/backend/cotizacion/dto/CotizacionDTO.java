package com.meowtfit.backend.cotizacion.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.meowtfit.backend.cotizacion.entity.EstadoCotizacion; 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionDTO {
    private Long idCotizacion;
    private LocalDateTime fechaCreacion;
    private EstadoCotizacion estado;
    private BigDecimal precioSugerido;
    private String sustento;
    private BigDecimal montoSugerido;
    private BigDecimal montoReal;

    // Relaciones (IDs y datos adicionales para el frontend)
    private Long idCliente;
    private String nombreCliente;
    private Long idComerciante;
    private String nombreComerciante;
    private Long idProducto;
    private String nombreProducto;

    private List<LineaCotizacionDTO> lineas;
    private List<ContrapropuestaDTO> contrapropuestas; 
}