package com.meowtfit.backend.catalogo.mapper;

import com.meowtfit.backend.catalogo.dto.ReglaDescuentoDTO;
import com.meowtfit.backend.catalogo.dto.ReglaDescuentoRequestDTO;
import com.meowtfit.backend.catalogo.entity.ReglaDescuento;
import com.meowtfit.backend.catalogo.entity.Producto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReglaDescuentoMapper {

    public ReglaDescuentoDTO toDTO(ReglaDescuento e) {
        if (e == null) return null;
        ReglaDescuentoDTO dto = new ReglaDescuentoDTO();
        dto.setIdRegla(e.getIdRegla());
        dto.setRangoMinimo(e.getRangoMinimo());
        dto.setRangoMaximo(e.getRangoMaximo());
        dto.setPorcentaje(e.getPorcentaje());
        if (e.getProducto() != null) dto.setIdProducto(e.getProducto().getIdProducto());
        return dto;
    }

    public ReglaDescuento toEntity(ReglaDescuentoDTO dto) {
        if (dto == null) return null;
        ReglaDescuento e = new ReglaDescuento();
        e.setIdRegla(dto.getIdRegla());
        e.setRangoMinimo(dto.getRangoMinimo());
        e.setRangoMaximo(dto.getRangoMaximo());
        e.setPorcentaje(dto.getPorcentaje());
        if (dto.getIdProducto() != null) {
            Producto p = new Producto();
            p.setIdProducto(dto.getIdProducto());
            e.setProducto(p);
        }
        return e;
    }

    public ReglaDescuento toEntity(ReglaDescuentoRequestDTO dto, Producto producto) {
        if (dto == null) return null;
        ReglaDescuento e = new ReglaDescuento();
        e.setRangoMinimo(dto.getRangoMinimo());
        e.setRangoMaximo(dto.getRangoMaximo());
        e.setPorcentaje(dto.getPorcentaje());
        e.setProducto(producto);
        return e;
    }
}