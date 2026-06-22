package com.meowtfit.backend.cotizacion.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionRequestDTO {

    private Long idUsuario;

    // idPedido puede ser nulo si la cotización se crea manualmente
    private Long idPedido;

    // idRegla puede ser nulo
    private Long idRegla;

    // Comentario inicial (opcional)
    @Size(max = 500, message = "El comentario no debe exceder los 500 caracteres")
    private String comentario;

    // Precio presupuesto (opcional, podría calcularse desde líneas)
    private BigDecimal precioPresupuesto;

    // Descuento global (opcional, podría ser 0)
    @Min(value = 0, message = "El descuento no puede ser negativo")
    private BigDecimal descuento = BigDecimal.ZERO;

    // Líneas de la cotización (obligatorias)
    @Valid
    private List<LineaCotizacionRequestDTO> lineas;
}