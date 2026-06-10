package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.CategoriaDTO;
import com.meowtfit.backend.catalogo.dto.CategoriaRequestDTO;

import java.util.List;

public interface CategoriaService {
    List<CategoriaDTO> listarTodas();
    CategoriaDTO registrarCategoria(CategoriaRequestDTO dto);
    CategoriaDTO editarCategoria(Long idCategoria, CategoriaRequestDTO dto);
    void eliminarCategoria(Long idCategoria);
}
