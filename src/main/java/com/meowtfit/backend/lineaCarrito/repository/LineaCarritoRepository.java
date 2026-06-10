package com.meowtfit.backend.lineaCarrito.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meowtfit.backend.lineaCarrito.entity.LineaCarrito;

@Repository
public interface LineaCarritoRepository extends JpaRepository<LineaCarrito, Long> {

    List<LineaCarrito> findByCarritoIdCarrito(Long idCarrito);

    List<LineaCarrito> findByVarianteProductoIdVariante(Long idVariante);
}
