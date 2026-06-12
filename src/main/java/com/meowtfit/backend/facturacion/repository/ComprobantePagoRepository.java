package com.meowtfit.backend.facturacion.repository;

import com.meowtfit.backend.facturacion.entity.ComprobantePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComprobantePagoRepository extends JpaRepository<ComprobantePago, Long> {
    Optional<ComprobantePago> findByPedidoIdPedido(Long idPedido);
    boolean existsByPedidoIdPedido(Long idPedido);
}
