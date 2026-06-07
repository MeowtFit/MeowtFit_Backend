package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.VarianteProductoDTO;

import java.util.List;

public interface VarianteProductoService {
    List<VarianteProductoDTO> obtenerVariantesPorProducto(Long idProducto);
}
