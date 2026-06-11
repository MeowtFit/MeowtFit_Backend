package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.VarianteProductoDTO;
import com.meowtfit.backend.catalogo.dto.VarianteProductoDetalleDTO;
import com.meowtfit.backend.catalogo.dto.VarianteProductoRequestDTO;

import java.util.List;

public interface VarianteProductoService {
    List<VarianteProductoDTO> obtenerVariantesPorProducto(Long idProducto);
    VarianteProductoDetalleDTO obtenerVariantePorId(Long idVariante);

    VarianteProductoDTO registrarVarianteProducto(VarianteProductoRequestDTO dto);
    VarianteProductoDTO editarVarianteProducto(Long idVariante, VarianteProductoRequestDTO dto);
    void eliminarVarianteProducto(Long idVariante);
}
