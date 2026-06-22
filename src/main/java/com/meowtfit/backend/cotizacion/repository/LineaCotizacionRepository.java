package com.meowtfit.backend.cotizacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meowtfit.backend.cotizacion.entity.LineaCotizacion;

@Repository
public interface LineaCotizacionRepository extends JpaRepository<LineaCotizacion, Long> {

    // Obtener líneas de una cotización
    List<LineaCotizacion> findByCotizacionIdCotizacion(Long idCotizacion);

    // Obtener líneas de una variante específica (para consultas de stock)
    List<LineaCotizacion> findByVarianteProductoIdVariante(Long idVariante);

    // Eliminar todas las líneas de una cotización (si se necesita en cascada manual)
    void deleteByCotizacionIdCotizacion(Long idCotizacion);
}