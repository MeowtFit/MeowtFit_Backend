package com.meowtfit.backend.pedido.mapper;

import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.meowtfit.backend.pedido.dto.LineaPedidoDTO;
import com.meowtfit.backend.pedido.dto.PedidoDTO;
import com.meowtfit.backend.pedido.entity.LineaPedido;
import com.meowtfit.backend.pedido.entity.Pedido;
import com.meowtfit.backend.color.entity.Color;

@Component
public class PedidoMapper {

    public PedidoDTO toDTO(Pedido pedido) {
        if (pedido == null) return null;

        PedidoDTO dto = new PedidoDTO();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaHoraRegistro(pedido.getFechaHoraRegistro());
        dto.setEstado(pedido.getEstado());
        dto.setMontoTotal(pedido.getMontoTotal());
        dto.setMetodoPago(pedido.getMetodoPago());
        dto.setDescuento(pedido.getDescuento());
        dto.setFechaEntrega(pedido.getFechaEntrega());
        dto.setIgv(pedido.getIgv());
        
        if (pedido.getUsuario() != null) {
            dto.setIdUsuario(pedido.getUsuario().getIdUsuario());
            dto.setNombreUsuario(pedido.getUsuario().getNombres());
        }

        if (pedido.getLineas() != null) {
            dto.setLineas(pedido.getLineas().stream().map(this::toLineaDTO).collect(Collectors.toList()));
        }

        return dto;
    }

    private LineaPedidoDTO toLineaDTO(LineaPedido linea) {
        LineaPedidoDTO dto = new LineaPedidoDTO();
        dto.setIdLineaPedido(linea.getIdLineaPedido());
        dto.setCantidad(linea.getCantidad());
        dto.setPrecioVenta(linea.getPrecioVenta());
        dto.setSubtotal(linea.getSubtotal());
        dto.setPrecioUnitario(linea.getPrecioUnitario());
        dto.setDescuentoAplicado(linea.getDescuentoAplicado());
        
        if (linea.getVariante() != null) {
            dto.setIdVariante(linea.getVariante().getIdVariante());
            dto.setTalla(linea.getVariante().getTalla());

            Color color = linea.getVariante().getColor();
            dto.setIdColor(color != null ? color.getIdColor() : null);
            dto.setColor(color != null ? color.getNombre() : null);

            if (linea.getVariante().getProducto() != null) {
                dto.setNombreProducto(linea.getVariante().getProducto().getNombre());
            }
        }
        return dto;
    }
}