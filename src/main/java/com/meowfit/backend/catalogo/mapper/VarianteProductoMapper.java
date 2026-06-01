package com.meowfit.backend.catalogo.mapper;

import com.meowfit.backend.catalogo.dto.VarianteProductoDTO;
import com.meowfit.backend.catalogo.entity.VarianteProducto;
import org.springframework.stereotype.Component;

@Component
public class VarianteProductoMapper {

    public VarianteProductoDTO toDTO(VarianteProducto variante) {
        if (variante == null) return null;
        return new VarianteProductoDTO(
            variante.getIdVariante(),
            variante.getTalla(),
            variante.getColor(),
            variante.getStockDisponible(),
            variante.getStockReservado()
        );
    }
}
