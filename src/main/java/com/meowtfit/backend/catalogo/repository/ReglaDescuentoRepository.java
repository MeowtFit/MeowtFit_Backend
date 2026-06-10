package com.meowtfit.backend.catalogo.repository;

import com.meowtfit.backend.catalogo.entity.ReglaDescuento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReglaDescuentoRepository extends JpaRepository<ReglaDescuento, Long> {
    List<ReglaDescuento> findByProductoIdProducto(Long idProducto);
}