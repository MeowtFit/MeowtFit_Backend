package com.meowtfit.backend.catalogo.mapper;

import com.meowtfit.backend.catalogo.dto.ProductoDTO;
import com.meowtfit.backend.catalogo.dto.ProductoRequestDTO;
import com.meowtfit.backend.catalogo.entity.Categoria;
import com.meowtfit.backend.catalogo.entity.Producto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductoMapper {

    private final CategoriaMapper categoriaMapper;
    private final VarianteProductoMapper varianteProductoMapper;
    private final ReglaDescuentoMapper reglaDescuentoMapper;

    public ProductoDTO toDTO(Producto producto) {
        if (producto == null) return null;
        
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombre(producto.getNombre());
        dto.setPrecioBase(producto.getPrecioBase());
        dto.setEstado(producto.getEstado());
        dto.setDescripcion(producto.getDescripcion());
        dto.setImagenUrl(producto.getImagenUrl());
        
        if (producto.getCategoria() != null) {
            dto.setCategoria(categoriaMapper.toDTO(producto.getCategoria()));
        }
        
        if (producto.getVariantes() != null) {
            dto.setVariantes(producto.getVariantes().stream()
                .map(varianteProductoMapper::toDTO)
                .collect(Collectors.toList()));
        }

        if (producto.getReglasDescuento() != null) {
            dto.setReglasDescuento(producto.getReglasDescuento().stream()
                .map(reglaDescuentoMapper::toDTO)
                .collect(Collectors.toList()));
        }

        return dto;
    }

    public Producto toEntity(ProductoRequestDTO dto, Categoria categoria) {
        if (dto == null) return null;

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecioBase(dto.getPrecioBase());
        producto.setEstado(dto.getEstado());
        producto.setDescripcion(dto.getDescripcion());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setCategoria(categoria);
        return producto;
    }
}
