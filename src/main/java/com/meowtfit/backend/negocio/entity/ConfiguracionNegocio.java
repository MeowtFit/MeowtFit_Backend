package com.meowtfit.backend.negocio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ConfiguracionNegocio")
public class ConfiguracionNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idConfiguracion")
    private Long idConfiguracion;

    @Column(name = "stockMinimoCotizacion", nullable = false)
    private Integer stockMinimoCotizacion = 1000;

    @Column(name = "porcentajePrecioPiso", precision = 5, scale = 2)
    private BigDecimal porcentajePrecioPiso = BigDecimal.ZERO;
}
