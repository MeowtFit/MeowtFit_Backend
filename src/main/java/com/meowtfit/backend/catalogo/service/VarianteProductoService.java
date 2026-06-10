package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.VarianteProductoDTO;
import com.meowtfit.backend.catalogo.dto.VarianteProductoRequestDTO;

import java.util.List;

public interface VarianteProductoService {
    List<VarianteProductoDTO> obtenerVariantesPorProducto(Long idProducto);
    
    VarianteProductoDTO registrarVarianteProducto(VarianteProductoRequestDTO dto);
    VarianteProductoDTO editarVarianteProducto(Long idVariante, VarianteProductoRequestDTO dto);
    void eliminarVarianteProducto(Long idVariante);
}
