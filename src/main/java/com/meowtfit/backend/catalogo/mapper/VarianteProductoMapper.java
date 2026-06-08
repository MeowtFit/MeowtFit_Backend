package com.meowtfit.backend.catalogo.mapper;

import com.meowtfit.backend.catalogo.dto.VarianteProductoDTO;
import com.meowtfit.backend.catalogo.dto.VarianteProductoRequestDTO;
import com.meowtfit.backend.catalogo.entity.Producto;
import com.meowtfit.backend.catalogo.entity.VarianteProducto;
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

    public VarianteProducto toEntity(VarianteProductoRequestDTO dto, Producto producto) {
        if (dto == null) return null;
        VarianteProducto variante = new VarianteProducto();
        variante.setTalla(dto.getTalla());
        variante.setColor(dto.getColor());
        variante.setStockDisponible(dto.getStockDisponible());
        variante.setStockReservado(dto.getStockReservado() != null ? dto.getStockReservado() : 0);
        variante.setProducto(producto);
        return variante;
    }
}
