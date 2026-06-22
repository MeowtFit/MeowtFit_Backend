package com.meowtfit.backend.cotizacion.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContrapropuestaRequestDTO {

    @NotNull(message = "El ID de la cotización es obligatorio")
    private Long idCotizacion;

    @NotNull(message = "El nuevo precio es obligatorio")
    @Min(value = 0, message = "El precio nuevo no puede ser negativo")
    private BigDecimal precioNuevo;

    @Size(max = 500, message = "El comentario no debe exceder los 500 caracteres")
    private String comentario;
}