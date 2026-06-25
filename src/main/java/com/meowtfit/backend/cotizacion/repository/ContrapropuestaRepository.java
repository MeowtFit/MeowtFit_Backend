package com.meowtfit.backend.cotizacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meowtfit.backend.cotizacion.entity.Contrapropuesta;

@Repository
public interface ContrapropuestaRepository extends JpaRepository<Contrapropuesta, Long> {

    List<Contrapropuesta> findByCotizacionIdCotizacionOrderByFechaCreacionDesc(Long idCotizacion);

    long countByCotizacionIdCotizacion(Long idCotizacion);

    Optional<Contrapropuesta> findFirstByCotizacionIdCotizacionOrderByFechaCreacionDesc(Long idCotizacion);
}