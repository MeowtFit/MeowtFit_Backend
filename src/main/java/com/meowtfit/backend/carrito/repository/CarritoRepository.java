package com.meowtfit.backend.carrito.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meowtfit.backend.carrito.entity.Carrito;
import com.meowtfit.backend.common.Estado;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioIdUsuarioAndEstado(Long idUsuario, Estado estado);

    List<Carrito> findByUsuarioIdUsuario(Long idUsuario);
}
