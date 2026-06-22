package com.meowtfit.backend.cotizacion.entity;

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
@Table(name = "LineaCotizacion")
public class LineaCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLineaCotizacion")
    private Long idLineaCotizacion;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precioUnitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "descuentoAplicado", precision = 10, scale = 2)
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;

    @Column(name = "stockDestinado")
    private Integer stockDestinado = 0;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCotizacion", nullable = false)
    private Cotizacion cotizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idVariante", nullable = false)
    private VarianteProducto varianteProducto;
}