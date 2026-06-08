package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.ReglaDescuentoDTO;
import com.meowtfit.backend.catalogo.entity.ReglaDescuento;
import com.meowtfit.backend.catalogo.entity.Producto;
import com.meowtfit.backend.catalogo.mapper.ReglaDescuentoMapper;
import com.meowtfit.backend.catalogo.repository.ReglaDescuentoRepository;
import com.meowtfit.backend.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReglaDescuentoServiceImpl implements ReglaDescuentoService {

    private final ReglaDescuentoRepository repo;
    private final ReglaDescuentoMapper mapper;

    @Override
    @Transactional
    public ReglaDescuentoDTO crearRegla(ReglaDescuentoDTO dto) {
        validarRangos(dto, null);
        ReglaDescuento entidad = mapper.toEntity(dto);
        // asegurar que producto sólo tiene id seteado (persistencia por referencia)
        entidad.setProducto(new Producto());
        entidad.getProducto().setIdProducto(dto.getIdProducto());
        return mapper.toDTO(repo.save(entidad));
    }

    @Override
    @Transactional
    public ReglaDescuentoDTO actualizarRegla(Long id, ReglaDescuentoDTO dto) {
        ReglaDescuento actual = repo.findById(id)
                .orElseThrow(() -> new BadRequestException("Regla no encontrada: " + id));
        dto.setIdRegla(id);
        validarRangos(dto, id);
        actual.setRangoMinimo(dto.getRangoMinimo());
        actual.setRangoMaximo(dto.getRangoMaximo());
        actual.setPorcentaje(dto.getPorcentaje());
        // producto no se actualiza aquí; si se necesita, añadir lógica
        return mapper.toDTO(repo.save(actual));
    }

    @Override
    public List<ReglaDescuentoDTO> listarPorProducto(Long idProducto) {
        return repo.findByProductoIdProducto(idProducto).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarRegla(Long id) {
        if (!repo.existsById(id)) throw new BadRequestException("Regla no encontrada: " + id);
        repo.deleteById(id);
    }

    private void validarRangos(ReglaDescuentoDTO dto, Long excludingId) {
        if (dto.getRangoMinimo() == null || dto.getRangoMaximo() == null) {
            throw new BadRequestException("Rangos obligatorios");
        }
        if (dto.getRangoMinimo() < 0 || dto.getRangoMaximo() < 0) {
            throw new BadRequestException("Rangos deben ser positivos");
        }
        if (dto.getRangoMinimo() > dto.getRangoMaximo()) {
            throw new BadRequestException("rangoMinimo debe ser <= rangoMaximo");
        }
        // obtener reglas existentes para el producto
        List<ReglaDescuento> existen = repo.findByProductoIdProducto(dto.getIdProducto());
        for (ReglaDescuento r : existen) {
            if (excludingId != null && r.getIdRegla().equals(excludingId)) continue;
            boolean solapa = dto.getRangoMinimo() <= r.getRangoMaximo()
                    && dto.getRangoMaximo() >= r.getRangoMinimo();
            if (solapa) {
                throw new BadRequestException("Rango solapa con regla existente (idRegla=" + r.getIdRegla() + ")");
            }
        }
    }
}