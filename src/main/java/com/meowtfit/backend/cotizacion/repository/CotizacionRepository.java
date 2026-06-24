package com.meowtfit.backend.cotizacion.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    // Buscar por estado
    List<Cotizacion> findByEstado(EstadoCotizacion estado);

    // Buscar por usuario
    List<Cotizacion> findByUsuario(Usuario usuario);

    // Buscar por usuario y estado (combinado)
    List<Cotizacion> findByUsuarioAndEstado(Usuario usuario, EstadoCotizacion estado);

    // Buscar cotización asociada a un pedido específico (si existe)
    Optional<Cotizacion> findByPedidoIdPedido(Long idPedido);

    // Buscar cotizaciones con estados en un rango (útil para listar pendientes, en_revisión, etc.)
    @Query("SELECT c FROM Cotizacion c WHERE c.estado IN :estados")
    List<Cotizacion> findByEstados(@Param("estados") List<EstadoCotizacion> estados);

    // Contar cotizaciones por estado (útil para dashboards)
    long countByEstado(EstadoCotizacion estado);
     // Filtrado con JPQL (sin Specification)
    @Query("SELECT c FROM Cotizacion c WHERE " +
           "(:estado IS NULL OR c.estado = :estado) AND " +
           "(:idUsuario IS NULL OR c.usuario.idUsuario = :idUsuario) AND " +
           "(:fechaDesde IS NULL OR c.fecha >= :fechaDesde) AND " +
           "(:fechaHasta IS NULL OR c.fecha <= :fechaHasta) AND " +
           "(:montoMin IS NULL OR c.montoTotal >= :montoMin) AND " +
           "(:montoMax IS NULL OR c.montoTotal <= :montoMax)")
    Page<Cotizacion> filtrarCotizaciones(
            @Param("estado") EstadoCotizacion estado,
            @Param("idUsuario") Long idUsuario,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta,
            @Param("montoMin") BigDecimal montoMin,
            @Param("montoMax") BigDecimal montoMax,
            Pageable pageable);
    
    // Filtrado para un usuario específico
    @Query("SELECT c FROM Cotizacion c WHERE " +
           "c.usuario.idUsuario = :idUsuario AND " +
           "(:estado IS NULL OR c.estado = :estado)")
    Page<Cotizacion> filtrarMisCotizaciones(
            @Param("idUsuario") Long idUsuario,
            @Param("estado") EstadoCotizacion estado,
            Pageable pageable);
            
    // Paginación por usuario
    Page<Cotizacion> findByUsuario(Usuario usuario, Pageable pageable);
    
    // Paginación por usuario y estado
    Page<Cotizacion> findByUsuarioAndEstado(Usuario usuario, EstadoCotizacion estado, Pageable pageable);
}