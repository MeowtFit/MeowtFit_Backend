package com.meowtfit.backend.cotizacion.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineaCotizacionDTO {
    private Long idLineaCotizacion;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    // Relaciones
    private Long idCotizacion;          // Opcional, pero útil para mapeo
    private Long idVariante;
    private String nombreProducto;      // Extra: para mostrar en frontend
    private String talla;
    private String color;               // Nombre del color
    private String colorHex;            // Código hexadecimal
}