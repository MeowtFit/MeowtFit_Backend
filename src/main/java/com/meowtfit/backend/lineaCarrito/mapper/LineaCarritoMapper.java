package com.meowtfit.backend.lineaCarrito.mapper;

import org.springframework.stereotype.Component;

import com.meowtfit.backend.carrito.entity.Carrito;
import com.meowtfit.backend.catalogo.entity.VarianteProducto;
import com.meowtfit.backend.lineaCarrito.dto.LineaCarritoDTO;
import com.meowtfit.backend.lineaCarrito.dto.LineaCarritoRequestDTO;
import com.meowtfit.backend.lineaCarrito.entity.LineaCarrito;

@Component
public class LineaCarritoMapper {

    public LineaCarritoDTO toDTO(LineaCarrito lineaCarrito) {
        if (lineaCarrito == null) return null;

        LineaCarritoDTO dto = new LineaCarritoDTO();
        dto.setIdLineaCarrito(lineaCarrito.getIdLineaCarrito());
        dto.setCantidad(lineaCarrito.getCantidad());
        dto.setPrecioUnitario(lineaCarrito.getPrecioUnitario());
        dto.setSubtotal(lineaCarrito.getSubtotal());
        dto.setIdCarrito(lineaCarrito.getCarrito() != null ? lineaCarrito.getCarrito().getIdCarrito() : null);
        dto.setIdVariante(lineaCarrito.getVarianteProducto() != null ? lineaCarrito.getVarianteProducto().getIdVariante() : null);
        return dto;
    }

    public LineaCarrito toEntity(LineaCarritoRequestDTO dto, Carrito carrito, VarianteProducto varianteProducto) {
        if (dto == null) return null;

        LineaCarrito linea = new LineaCarrito();
        linea.setCantidad(dto.getCantidad());
        linea.setPrecioUnitario(dto.getPrecioUnitario());
        linea.setSubtotal(dto.getSubtotal());
        linea.setCarrito(carrito);
        linea.setVarianteProducto(varianteProducto);
        return linea;
    }
}
