package com.meowtfit.backend.pedido.entity;

import java.math.BigDecimal;

import com.meowtfit.backend.catalogo.entity.VarianteProducto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LineaPedido")
public class LineaPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLineaPedido")
    private Long idLineaPedido;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precioVenta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "precioUnitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "descuentoAplicado", precision = 10, scale = 2)
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idVariante", nullable = false)
    private VarianteProducto variante;
}