package com.meowtfit.backend.catalogo.dto;

import com.meowtfit.backend.color.dto.ColorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarianteProductoDetalleDTO {
    private Long idVariante;
    private String talla;
    private Integer stockDisponible;
    private Integer stockReservado;
    private ColorDTO color;
    private ProductoResumenDTO producto;
}
