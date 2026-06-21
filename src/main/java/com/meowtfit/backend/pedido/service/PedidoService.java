package com.meowtfit.backend.pedido.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.meowtfit.backend.pedido.dto.PedidoDTO;
import com.meowtfit.backend.pedido.dto.PedidoRequestDTO;
import com.meowtfit.backend.pedido.entity.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PedidoService {
    PedidoDTO crearPedido(PedidoRequestDTO dto, String correoUsuario);
    PedidoDTO obtenerPorId(Long idPedido);
    List<PedidoDTO> listarPorUsuario(String correoUsuario);
    List<PedidoDTO> listarTodos();
    PedidoDTO cambiarEstado(Long idPedido, EstadoPedido nuevoEstado);

    // Filtrado paginado para admin (todos los pedidos)
    Page<PedidoDTO> filtrarPedidos(EstadoPedido estado, Long idUsuario,
        LocalDateTime fechaDesde, LocalDateTime fechaHasta,
        BigDecimal montoMin, BigDecimal montoMax, Pageable pageable);

    // Filtrado paginado para el cliente (solo sus pedidos)
    Page<PedidoDTO> filtrarMisPedidos(String correoUsuario, EstadoPedido estado, Pageable pageable);
}