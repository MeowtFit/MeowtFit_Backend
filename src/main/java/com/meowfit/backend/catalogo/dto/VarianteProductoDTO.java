package com.meowfit.backend.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarianteProductoDTO {
    private Long idVariante;
    private String talla;
    private String color;
    private Integer stockDisponible;
    private Integer stockReservado;
}
