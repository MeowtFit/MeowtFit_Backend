package com.meowtfit.backend.pedido.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.meowtfit.backend.pedido.entity.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Long idPedido;
    private LocalDateTime fechaHoraRegistro;
    private EstadoPedido estado;
    private BigDecimal montoTotal;
    private String metodoPago;
    private BigDecimal descuento;
    private LocalDate fechaEntrega;
    private BigDecimal igv;
    private Long idUsuario;
    private String nombreUsuario; // Extra para el frontend
    private List<LineaPedidoDTO> lineas;
}