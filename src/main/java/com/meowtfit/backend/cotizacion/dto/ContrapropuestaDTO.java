package com.meowtfit.backend.cotizacion.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContrapropuestaDTO {
    private Long idContrapropuesta;
    private String comentario;
    private LocalDateTime fecha;
    private BigDecimal precioNuevo;

    private Long idCotizacion;   // Para saber a qué cotización pertenece
}