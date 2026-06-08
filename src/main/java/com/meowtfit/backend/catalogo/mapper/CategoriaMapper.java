package com.meowtfit.backend.catalogo.mapper;

import com.meowtfit.backend.catalogo.dto.CategoriaDTO;
import com.meowtfit.backend.catalogo.dto.CategoriaRequestDTO;
import com.meowtfit.backend.catalogo.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaDTO toDTO(Categoria categoria) {
        if (categoria == null) return null;
        return new CategoriaDTO(categoria.getIdCategoria(), categoria.getNombre());
    }

    public Categoria toEntity(CategoriaDTO dto) {
        if (dto == null) return null;
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(dto.getIdCategoria());
        categoria.setNombre(dto.getNombre());
        return categoria;
    }

    public Categoria toEntity(CategoriaRequestDTO dto) {
        if (dto == null) return null;
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        return categoria;
    }
}
