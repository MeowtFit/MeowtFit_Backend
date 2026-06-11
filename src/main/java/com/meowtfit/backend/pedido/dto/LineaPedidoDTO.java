package com.meowtfit.backend.pedido.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineaPedidoDTO {
    private Long idLineaPedido;
    private Integer cantidad;
    private BigDecimal precioVenta;
    private BigDecimal subtotal;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoAplicado;
    private Long idVariante;
    private String nombreProducto; // Dato extra útil para el frontend
    private String talla;
    private Long idColor;
    private String color; // nombre del color para mostrar en el frontend
}