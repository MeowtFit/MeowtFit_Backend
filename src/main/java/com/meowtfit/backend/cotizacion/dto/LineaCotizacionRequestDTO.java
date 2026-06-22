package com.meowtfit.backend.cotizacion.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineaCotizacionRequestDTO {

    @NotNull(message = "El ID de la variante es obligatorio")
    private Long idVariante;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 0, message = "El precio unitario no puede ser negativo")
    private BigDecimal precioUnitario;

    // El descuento aplicado por línea (opcional, se puede calcular desde reglas)
    @Min(value = 0, message = "El descuento aplicado no puede ser negativo")
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;
}