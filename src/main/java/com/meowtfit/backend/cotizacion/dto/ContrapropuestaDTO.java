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
    private String sustento;
    private LocalDateTime fechaCreacion;
    private BigDecimal precioNuevo;

    private Long idCotizacion;
    private Long idUserGenerador;
    private String nombreUserGenerador;
}