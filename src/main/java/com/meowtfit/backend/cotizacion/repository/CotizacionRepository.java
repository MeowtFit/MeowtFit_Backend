package com.meowtfit.backend.cotizacion.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.meowtfit.backend.cotizacion.entity.Cotizacion;
import com.meowtfit.backend.cotizacion.entity.EstadoCotizacion;
import com.meowtfit.backend.usuario.entity.Usuario;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    List<Cotizacion> findByEstado(EstadoCotizacion estado);

    List<Cotizacion> findByCliente(Usuario cliente);

    List<Cotizacion> findByComerciante(Usuario comerciante);

    List<Cotizacion> findByClienteAndEstado(Usuario cliente, EstadoCotizacion estado);

    @Query("SELECT c FROM Cotizacion c WHERE c.estado IN :estados")
    List<Cotizacion> findByEstados(@Param("estados") List<EstadoCotizacion> estados);

    long countByEstado(EstadoCotizacion estado);
    
    @Query("SELECT c FROM Cotizacion c WHERE " +
           "(:estado IS NULL OR c.estado = :estado) AND " +
           "(:idCliente IS NULL OR c.cliente.idUsuario = :idCliente) AND " +
           "(:idComerciante IS NULL OR c.comerciante.idUsuario = :idComerciante) AND " +
           "(:fechaDesde IS NULL OR c.fechaCreacion >= :fechaDesde) AND " +
           "(:fechaHasta IS NULL OR c.fechaCreacion <= :fechaHasta) AND " +
           "(:montoMin IS NULL OR c.montoSugerido >= :montoMin) AND " +
           "(:montoMax IS NULL OR c.montoSugerido <= :montoMax)")
    Page<Cotizacion> filtrarCotizaciones(
            @Param("estado") EstadoCotizacion estado,
            @Param("idCliente") Long idCliente,
            @Param("idComerciante") Long idComerciante,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta,
            @Param("montoMin") BigDecimal montoMin,
            @Param("montoMax") BigDecimal montoMax,
            Pageable pageable);
    
    @Query("SELECT c FROM Cotizacion c WHERE " +
           "c.cliente.idUsuario = :idCliente AND " +
           "(:estado IS NULL OR c.estado = :estado)")
    Page<Cotizacion> filtrarMisCotizaciones(
            @Param("idCliente") Long idCliente,
            @Param("estado") EstadoCotizacion estado,
            Pageable pageable);
            
    Page<Cotizacion> findByCliente(Usuario cliente, Pageable pageable);
    
    Page<Cotizacion> findByClienteAndEstado(Usuario cliente, EstadoCotizacion estado, Pageable pageable);
}