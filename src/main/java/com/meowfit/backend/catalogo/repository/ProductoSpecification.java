package com.meowfit.backend.catalogo.repository;

import com.meowfit.backend.catalogo.entity.Producto;
import com.meowfit.backend.catalogo.entity.VarianteProducto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductoSpecification {

    public static Specification<Producto> hasCategoria(Long idCategoria) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("categoria").get("idCategoria"), idCategoria);
    }

    public static Specification<Producto> precioBetween(BigDecimal precioMin, BigDecimal precioMax) {
        return (root, query, criteriaBuilder) -> {
            if (precioMin != null && precioMax != null) {
                return criteriaBuilder.between(root.get("precioBase"), precioMin, precioMax);
            } else if (precioMin != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("precioBase"), precioMin);
            } else if (precioMax != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("precioBase"), precioMax);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Producto> hasTallaDisponible(String talla) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Join<Producto, VarianteProducto> variantes = root.join("variantes", JoinType.INNER);
            return criteriaBuilder.and(
                criteriaBuilder.equal(variantes.get("talla"), talla),
                criteriaBuilder.greaterThan(variantes.get("stockDisponible"), 0)
            );
        };
    }
}
