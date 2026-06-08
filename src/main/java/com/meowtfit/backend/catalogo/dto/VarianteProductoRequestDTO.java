package com.meowtfit.backend.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarianteProductoRequestDTO {
    private String talla;
    private String color;
    private Integer stockDisponible;
    private Integer stockReservado;
    private Long idProducto;
}
