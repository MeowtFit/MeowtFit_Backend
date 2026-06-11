package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.VarianteProductoDTO;
import com.meowtfit.backend.catalogo.dto.VarianteProductoDetalleDTO;
import com.meowtfit.backend.catalogo.dto.VarianteProductoRequestDTO;
import com.meowtfit.backend.catalogo.entity.Producto;
import com.meowtfit.backend.catalogo.entity.VarianteProducto;
import com.meowtfit.backend.catalogo.mapper.VarianteProductoMapper;
import com.meowtfit.backend.catalogo.repository.ProductoRepository;
import com.meowtfit.backend.catalogo.repository.VarianteProductoRepository;
import com.meowtfit.backend.color.repository.ColorRepository;
import com.meowtfit.backend.exception.BadRequestException;
import com.meowtfit.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VarianteProductoServiceImpl implements VarianteProductoService {

    private final VarianteProductoRepository varianteProductoRepository;
    private final VarianteProductoMapper varianteProductoMapper;
    private final ProductoRepository productoRepository;
    private final ColorRepository colorRepository;

    @Override
    public List<VarianteProductoDTO> obtenerVariantesPorProducto(Long idProducto) {
        // Find by idProducto will require a method in the repository. Let's add that next.
        return varianteProductoRepository.findByProducto_IdProducto(idProducto).stream()
                .map(varianteProductoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VarianteProductoDetalleDTO obtenerVariantePorId(Long idVariante) {
        VarianteProducto variante = varianteProductoRepository.findById(idVariante)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada con id: " + idVariante));
        return varianteProductoMapper.toDetalleDTO(variante);
    }

    @Override
    @Transactional
    public VarianteProductoDTO registrarVarianteProducto(VarianteProductoRequestDTO dto) {
        if (dto.getIdProducto() == null) {
            throw new BadRequestException("El id del producto es obligatorio");
        }
        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + dto.getIdProducto()));
        if (dto.getIdColor() == null) {
            throw new BadRequestException("El id del color es obligatorio");
        }
        var color = colorRepository.findById(dto.getIdColor())
                .orElseThrow(() -> new ResourceNotFoundException("Color no encontrado con id: " + dto.getIdColor()));

        VarianteProducto variante = varianteProductoMapper.toEntity(dto, producto, color);
        VarianteProducto guardado = varianteProductoRepository.save(variante);
        return varianteProductoMapper.toDTO(guardado);
    }

    @Override
    @Transactional
    public VarianteProductoDTO editarVarianteProducto(Long idVariante, VarianteProductoRequestDTO dto) {
        VarianteProducto variante = varianteProductoRepository.findById(idVariante)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada con id: " + idVariante));

        if (dto.getIdProducto() == null) {
            throw new BadRequestException("El id del producto es obligatorio");
        }
        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + dto.getIdProducto()));
        if (dto.getIdColor() == null) {
            throw new BadRequestException("El id del color es obligatorio");
        }
        var color = colorRepository.findById(dto.getIdColor())
                .orElseThrow(() -> new ResourceNotFoundException("Color no encontrado con id: " + dto.getIdColor()));

        variante.setTalla(dto.getTalla());
        variante.setColor(color);
        variante.setStockDisponible(dto.getStockDisponible());
        variante.setStockReservado(dto.getStockReservado() != null ? dto.getStockReservado() : 0);
        variante.setProducto(producto);

        VarianteProducto guardado = varianteProductoRepository.save(variante);
        return varianteProductoMapper.toDTO(guardado);
    }

    @Override
    @Transactional
    public void eliminarVarianteProducto(Long idVariante) {
        VarianteProducto variante = varianteProductoRepository.findById(idVariante)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada con id: " + idVariante));
        varianteProductoRepository.delete(variante);
    }
}
