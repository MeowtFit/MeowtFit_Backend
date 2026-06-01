package com.meowfit.backend.catalogo.mapper;

import com.meowfit.backend.catalogo.dto.CategoriaDTO;
import com.meowfit.backend.catalogo.entity.Categoria;
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
}
