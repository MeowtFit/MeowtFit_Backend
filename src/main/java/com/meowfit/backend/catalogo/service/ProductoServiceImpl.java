package com.meowfit.backend.catalogo.service;

import com.meowfit.backend.catalogo.dto.ProductoDTO;
import com.meowfit.backend.catalogo.entity.Producto;
import com.meowfit.backend.catalogo.mapper.ProductoMapper;
import com.meowfit.backend.catalogo.repository.ProductoRepository;
import com.meowfit.backend.catalogo.repository.ProductoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
// Para que mantenga la sesión abierta durante el ciclo de vida
// del método filtrarProductosActivos
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    public Page<ProductoDTO> filtrarProductos(Long idCategoria, BigDecimal precioMin, BigDecimal precioMax, String talla, Pageable pageable) {
        Specification<Producto> spec = null;

        if (idCategoria != null) {
            spec = Specification.where(ProductoSpecification.hasCategoria(idCategoria));
        }
        
        if (precioMin != null || precioMax != null) {
            Specification<Producto> precioSpec = ProductoSpecification.precioBetween(precioMin, precioMax);
            spec = (spec == null) ? Specification.where(precioSpec) : spec.and(precioSpec);
        }
        
        if (talla != null && !talla.trim().isEmpty()) {
            Specification<Producto> tallaSpec = ProductoSpecification.hasTallaDisponible(talla.trim());
            spec = (spec == null) ? Specification.where(tallaSpec) : spec.and(tallaSpec);
        }

        if (spec == null) {
            return productoRepository.findAll(pageable).map(productoMapper::toDTO);
        }
        return productoRepository.findAll(spec, pageable).map(productoMapper::toDTO);
    }
}
