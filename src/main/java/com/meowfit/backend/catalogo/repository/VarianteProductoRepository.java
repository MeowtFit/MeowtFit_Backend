package com.meowfit.backend.catalogo.repository;

import com.meowfit.backend.catalogo.entity.VarianteProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, Long> {
}
