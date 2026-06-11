package com.meowtfit.backend.pedido.service;

import java.util.List;
import com.meowtfit.backend.pedido.dto.PedidoDTO;
import com.meowtfit.backend.pedido.dto.PedidoRequestDTO;
import com.meowtfit.backend.pedido.entity.EstadoPedido;

public interface PedidoService {
    PedidoDTO crearPedido(PedidoRequestDTO dto, String correoUsuario);
    PedidoDTO obtenerPorId(Long idPedido);
    List<PedidoDTO> listarPorUsuario(String correoUsuario);
    List<PedidoDTO> listarTodos();
    PedidoDTO cambiarEstado(Long idPedido, EstadoPedido nuevoEstado);
}