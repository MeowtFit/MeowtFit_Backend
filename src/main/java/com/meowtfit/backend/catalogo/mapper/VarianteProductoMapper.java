package com.meowtfit.backend.catalogo.mapper;

import com.meowtfit.backend.catalogo.dto.ProductoResumenDTO;
import com.meowtfit.backend.catalogo.dto.VarianteProductoDTO;
import com.meowtfit.backend.catalogo.dto.VarianteProductoDetalleDTO;
import com.meowtfit.backend.catalogo.dto.VarianteProductoRequestDTO;
import com.meowtfit.backend.catalogo.entity.Producto;
import com.meowtfit.backend.catalogo.entity.VarianteProducto;
import com.meowtfit.backend.color.entity.Color;
import com.meowtfit.backend.color.mapper.ColorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VarianteProductoMapper {

    private final ColorMapper colorMapper;

    public VarianteProductoDTO toDTO(VarianteProducto variante) {
        if (variante == null) return null;
        VarianteProductoDTO dto = new VarianteProductoDTO();
        dto.setIdVariante(variante.getIdVariante());
        dto.setTalla(variante.getTalla());
        dto.setIdColor(variante.getColor() != null ? variante.getColor().getIdColor() : null);
        dto.setColor(variante.getColor() != null ? colorMapper.toDTO(variante.getColor()) : null);
        dto.setStockDisponible(variante.getStockDisponible());
        dto.setStockReservado(variante.getStockReservado());
        return dto;
    }

    public VarianteProductoDetalleDTO toDetalleDTO(VarianteProducto variante) {
        if (variante == null) return null;

        VarianteProductoDetalleDTO dto = new VarianteProductoDetalleDTO();
        dto.setIdVariante(variante.getIdVariante());
        dto.setTalla(variante.getTalla());
        dto.setStockDisponible(variante.getStockDisponible());
        dto.setStockReservado(variante.getStockReservado());
        dto.setColor(variante.getColor() != null ? colorMapper.toDTO(variante.getColor()) : null);

        if (variante.getProducto() != null) {
            ProductoResumenDTO productoDTO = new ProductoResumenDTO();
            productoDTO.setIdProducto(variante.getProducto().getIdProducto());
            productoDTO.setNombre(variante.getProducto().getNombre());
            productoDTO.setPrecioBase(variante.getProducto().getPrecioBase());
            productoDTO.setImagenUrl(variante.getProducto().getImagenUrl());
            dto.setProducto(productoDTO);
        }

        return dto;
    }

    public VarianteProducto toEntity(VarianteProductoRequestDTO dto, Producto producto, Color color) {
        if (dto == null) return null;
        VarianteProducto variante = new VarianteProducto();
        variante.setTalla(dto.getTalla());
        variante.setColor(color);
        variante.setStockDisponible(dto.getStockDisponible());
        variante.setStockReservado(dto.getStockReservado() != null ? dto.getStockReservado() : 0);
        variante.setProducto(producto);
        return variante;
    }
}
