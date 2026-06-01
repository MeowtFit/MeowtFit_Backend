package com.meowfit.backend.catalogo.service;

import com.meowfit.backend.catalogo.dto.VarianteProductoDTO;

import java.util.List;

public interface VarianteProductoService {
    List<VarianteProductoDTO> obtenerVariantesPorProducto(Long idProducto);
}
