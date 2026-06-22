package com.meowtfit.backend.cotizacion.repository;

import java.util.List;
import java.util.Optional;

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
}