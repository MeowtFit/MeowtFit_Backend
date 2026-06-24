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
    private LocalDateTime fecha;
    private EstadoCotizacion estado;
    private BigDecimal precioPresupuesto;
    private String comentario;
    private BigDecimal montoTotal;
    private BigDecimal descuento;

    // Relaciones (IDs y datos adicionales para el frontend)
    private Long idUsuario;
    private String nombreUsuario;          // Extra: nombre del cliente
    private Long idRegla;                  // Puede ser null
    private Long idPedido;                 // Puede ser null

    private List<LineaCotizacionDTO> lineas;
    private List<ContrapropuestaDTO> contrapropuestas; // Opcional, puede incluirse o no
}