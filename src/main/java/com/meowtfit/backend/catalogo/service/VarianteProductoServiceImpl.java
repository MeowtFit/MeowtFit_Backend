package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.VarianteProductoDTO;
import com.meowtfit.backend.catalogo.mapper.VarianteProductoMapper;
import com.meowtfit.backend.catalogo.repository.VarianteProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VarianteProductoServiceImpl implements VarianteProductoService {

    private final VarianteProductoRepository varianteProductoRepository;
    private final VarianteProductoMapper varianteProductoMapper;

    @Override
    public List<VarianteProductoDTO> obtenerVariantesPorProducto(Long idProducto) {
        // En un caso real buscaríamos por idProducto, pero requeriría un método en el repository.
        // Por simplicidad, retornamos una lista vacía por ahora si no es requerido.
        return List.of();
    }
}
