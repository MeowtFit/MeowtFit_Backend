package com.meowtfit.backend.cotizacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meowtfit.backend.cotizacion.entity.Contrapropuesta;

@Repository
public interface ContrapropuestaRepository extends JpaRepository<Contrapropuesta, Long> {

    // Obtener todas las contrapropuestas de una cotización, ordenadas por fecha (más reciente primero)
    List<Contrapropuesta> findByCotizacionIdCotizacionOrderByFechaDesc(Long idCotizacion);

    // Contar cuántas contrapropuestas tiene una cotización (para validar el límite de 5)
    long countByCotizacionIdCotizacion(Long idCotizacion);

    // Obtener la última contrapropuesta de una cotización (para mostrar la oferta actual)
    Optional<Contrapropuesta> findFirstByCotizacionIdCotizacionOrderByFechaDesc(Long idCotizacion);
}