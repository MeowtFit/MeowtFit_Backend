package com.meowtfit.backend.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReglaDescuentoRequestDTO {
    private Integer rangoMinimo;
    private Integer rangoMaximo;
    private BigDecimal porcentaje;
}
