package com.meowfit.backend.catalogo.service;

import com.meowfit.backend.catalogo.dto.ProductoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductoService {
    Page<ProductoDTO> filtrarProductos(Long idCategoria, BigDecimal precioMin, BigDecimal precioMax, String talla, Pageable pageable);
}
