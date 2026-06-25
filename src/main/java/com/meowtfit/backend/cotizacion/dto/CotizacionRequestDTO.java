package com.meowtfit.backend.cotizacion.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionRequestDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "El precio sugerido es obligatorio")
    @Min(value = 0, message = "El precio sugerido no puede ser negativo")
    private BigDecimal precioSugerido;

    @NotNull(message = "El sustento es obligatorio")
    @Size(max = 1000, message = "El sustento no debe exceder los 1000 caracteres")
    private String sustento;

    @Valid
    private List<LineaCotizacionRequestDTO> lineas;
}