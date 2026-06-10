package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.ProductoDTO;
import com.meowtfit.backend.catalogo.dto.ProductoRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoService {
    Page<ProductoDTO> filtrarProductos(String nombre, Long idCategoria, BigDecimal precioMin, BigDecimal precioMax, String talla, Pageable pageable);

    ProductoDTO registrarProducto(ProductoRequestDTO dto);
    ProductoDTO editarProducto(Long idProducto, ProductoRequestDTO dto);
    ProductoDTO buscarProductoPorId(Long idProducto);
    
    ProductoDTO activarProducto(Long idProducto);
    ProductoDTO desactivarProducto(Long idProducto);
    
    List<ProductoDTO> listarProductosPorCategoria(Long idCategoria);
    List<ProductoDTO> listarProductosPorNombre(String nombre);
}
