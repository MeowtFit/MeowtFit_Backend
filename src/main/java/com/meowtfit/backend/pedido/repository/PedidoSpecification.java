package com.meowtfit.backend.pedido.repository;

import com.meowtfit.backend.pedido.entity.EstadoPedido;
import com.meowtfit.backend.pedido.entity.Pedido;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoSpecification {

    public static Specification<Pedido> hasEstado(EstadoPedido estado) {
        return (root, query, cb) -> cb.equal(root.get("estado"), estado);
    }

    public static Specification<Pedido> hasUsuario(Long idUsuario) {
        return (root, query, cb) ->
            cb.equal(root.get("usuario").get("idUsuario"), idUsuario);
    }

    public static Specification<Pedido> fechaBetween(LocalDateTime desde, LocalDateTime hasta) {
        return (root, query, cb) -> {
            if (desde != null && hasta != null)
                return cb.between(root.get("fechaHoraRegistro"), desde, hasta);
            if (desde != null)
                return cb.greaterThanOrEqualTo(root.get("fechaHoraRegistro"), desde);
            if (hasta != null)
                return cb.lessThanOrEqualTo(root.get("fechaHoraRegistro"), hasta);
            return cb.conjunction();
        };
    }

    public static Specification<Pedido> montoTotalBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min != null && max != null)
                return cb.between(root.get("montoTotal"), min, max);
            if (min != null)
                return cb.greaterThanOrEqualTo(root.get("montoTotal"), min);
            if (max != null)
                return cb.lessThanOrEqualTo(root.get("montoTotal"), max);
            return cb.conjunction();
        };
    }
}
