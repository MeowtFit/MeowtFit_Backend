package com.meowtfit.backend.pedido.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

    @NotEmpty(message = "El pedido debe tener al menos una línea")
    @Valid
    private List<LineaPedidoRequestDTO> lineas;
}