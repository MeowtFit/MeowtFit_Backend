package com.meowtfit.backend.negocio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionNegocioDTO {
    private Long idConfiguracion;
    private Integer stockMinimoCotizacion;
    private BigDecimal porcentajePrecioPiso;
}
