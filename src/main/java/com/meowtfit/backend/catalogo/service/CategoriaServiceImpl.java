package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.CategoriaDTO;
import com.meowtfit.backend.catalogo.dto.CategoriaRequestDTO;
import com.meowtfit.backend.catalogo.entity.Categoria;
import com.meowtfit.backend.catalogo.mapper.CategoriaMapper;
import com.meowtfit.backend.catalogo.repository.CategoriaRepository;
import com.meowtfit.backend.exception.BadRequestException;
import com.meowtfit.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoriaDTO registrarCategoria(CategoriaRequestDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre de la categoría no puede estar vacío");
        }
        Categoria categoria = categoriaMapper.toEntity(dto);
        Categoria guardado = categoriaRepository.save(categoria);
        return categoriaMapper.toDTO(guardado);
    }

    @Override
    public CategoriaDTO editarCategoria(Long idCategoria, CategoriaRequestDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre de la categoría no puede estar vacío");
        }
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + idCategoria));
        
        categoria.setNombre(dto.getNombre());
        Categoria guardado = categoriaRepository.save(categoria);
        return categoriaMapper.toDTO(guardado);
    }

    @Override
    public void eliminarCategoria(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + idCategoria));
        categoriaRepository.delete(categoria);
    }
}
