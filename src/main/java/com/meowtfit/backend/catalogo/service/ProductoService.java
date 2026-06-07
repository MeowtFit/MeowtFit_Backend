package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.ProductoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductoService {
    Page<ProductoDTO> filtrarProductos(Long idCategoria, BigDecimal precioMin, BigDecimal precioMax, String talla, Pageable pageable);
}
