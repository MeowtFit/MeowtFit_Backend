package com.meowtfit.backend.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReglaDescuentoDTO {
    private Long idRegla;
    private Integer rangoMinimo;
    private Integer rangoMaximo;
    private BigDecimal porcentaje;
    private Long idProducto;
}