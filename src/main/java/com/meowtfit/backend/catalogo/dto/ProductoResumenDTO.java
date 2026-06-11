package com.meowtfit.backend.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResumenDTO {
    private Long idProducto;
    private String nombre;
    private BigDecimal precioBase;
    private String imagenUrl;
}
