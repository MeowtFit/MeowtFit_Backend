package com.meowtfit.backend.pedido.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meowtfit.backend.catalogo.entity.VarianteProducto;
import com.meowtfit.backend.catalogo.repository.VarianteProductoRepository;
import com.meowtfit.backend.exception.BadRequestException;
import com.meowtfit.backend.exception.ResourceNotFoundException;
import com.meowtfit.backend.pedido.dto.LineaPedidoRequestDTO;
import com.meowtfit.backend.pedido.dto.PedidoDTO;
import com.meowtfit.backend.pedido.dto.PedidoRequestDTO;
import com.meowtfit.backend.pedido.entity.EstadoPedido;
import com.meowtfit.backend.pedido.entity.LineaPedido;
import com.meowtfit.backend.pedido.entity.Pedido;
import com.meowtfit.backend.pedido.mapper.PedidoMapper;
import com.meowtfit.backend.pedido.repository.PedidoRepository;
import com.meowtfit.backend.pedido.repository.PedidoSpecification;
import com.meowtfit.backend.usuario.entity.Usuario;
import com.meowtfit.backend.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoMapper pedidoMapper;

    @Override
    @Transactional
    public PedidoDTO crearPedido(PedidoRequestDTO dto, String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setMetodoPago(dto.getMetodoPago());
        pedido.setEstado(EstadoPedido.REGISTRADO);
        pedido.setDescuento(BigDecimal.ZERO); // Podrías agregar lógica de cupones después
        pedido.setFechaEntrega(LocalDate.now().plusDays(3)); // Ejemplo: entrega en 3 días

        BigDecimal montoTotal = BigDecimal.ZERO;

        for (LineaPedidoRequestDTO lineaDto : dto.getLineas()) {
            VarianteProducto variante = varianteProductoRepository.findById(lineaDto.getIdVariante())
                    .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada con id: " + lineaDto.getIdVariante()));

            // Validación crucial de stock
            if (variante.getStockDisponible() < lineaDto.getCantidad()) {
                throw new BadRequestException("Stock insuficiente para el producto: " + 
                    variante.getProducto().getNombre() + " (Talla: " + variante.getTalla() + ")");
            }

            // Actualizar stock: sale de disponible y entra a reservado
            variante.setStockDisponible(variante.getStockDisponible() - lineaDto.getCantidad());
            variante.setStockReservado(variante.getStockReservado() + lineaDto.getCantidad());
            varianteProductoRepository.save(variante);

            // Cálculos matemáticos
            BigDecimal precioUnitario = variante.getProducto().getPrecioBase();
            BigDecimal descuentoAplicado = BigDecimal.ZERO; // Sin reglas de descuento por ahora
            BigDecimal precioVenta = precioUnitario.subtract(descuentoAplicado);
            BigDecimal subtotal = precioVenta.multiply(new BigDecimal(lineaDto.getCantidad()));

            // Armar la línea
            LineaPedido linea = new LineaPedido();
            linea.setVariante(variante);
            linea.setCantidad(lineaDto.getCantidad());
            linea.setPrecioUnitario(precioUnitario);
            linea.setDescuentoAplicado(descuentoAplicado);
            linea.setPrecioVenta(precioVenta);
            linea.setSubtotal(subtotal);

            pedido.addLinea(linea);
            montoTotal = montoTotal.add(subtotal);
        }

        // Aplicar IGV (18% en Perú, asumiendo que el precioBase NO incluye IGV. 
        // Si ya lo incluye, la fórmula sería igv = montoTotal - (montoTotal / 1.18))
        BigDecimal igv = montoTotal.multiply(new BigDecimal("0.18"));
        
        pedido.setMontoTotal(montoTotal.add(igv)); // Total final a pagar
        pedido.setIgv(igv);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        return pedidoMapper.toDTO(pedidoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDTO obtenerPorId(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        return pedidoMapper.toDTO(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPorUsuario(String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return pedidoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream().map(pedidoMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarTodos() {
        return pedidoRepository.findAll()
                .stream().map(pedidoMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PedidoDTO cambiarEstado(Long idPedido, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        
        // Lógica opcional: Si el pedido se CANCELA o rechaza, devolver el stock reservado a disponible
        if ((nuevoEstado == EstadoPedido.CANCELADO || nuevoEstado == EstadoPedido.PAGO_RECHAZADO) 
            && pedido.getEstado() != EstadoPedido.CANCELADO && pedido.getEstado() != EstadoPedido.PAGO_RECHAZADO) {
            
            for (LineaPedido linea : pedido.getLineas()) {
                VarianteProducto variante = linea.getVariante();
                variante.setStockDisponible(variante.getStockDisponible() + linea.getCantidad());
                variante.setStockReservado(variante.getStockReservado() - linea.getCantidad());
                varianteProductoRepository.save(variante);
            }
        }

        // Lógica opcional: Si el pedido se ENVIA, el stock reservado se reduce (ya salió del almacén)
        if (nuevoEstado == EstadoPedido.ENVIADO && pedido.getEstado() != EstadoPedido.ENVIADO) {
             for (LineaPedido linea : pedido.getLineas()) {
                VarianteProducto variante = linea.getVariante();
                variante.setStockReservado(variante.getStockReservado() - linea.getCantidad());
                varianteProductoRepository.save(variante);
            }
        }

        pedido.setEstado(nuevoEstado);
        return pedidoMapper.toDTO(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoDTO> filtrarPedidos(EstadoPedido estado, Long idUsuario,
            LocalDateTime fechaDesde, LocalDateTime fechaHasta,
            BigDecimal montoMin, BigDecimal montoMax, Pageable pageable) {

        Specification<Pedido> spec = Specification.where((Specification<Pedido>) null);

        if (estado != null)
            spec = spec.and(PedidoSpecification.hasEstado(estado));
        if (idUsuario != null)
            spec = spec.and(PedidoSpecification.hasUsuario(idUsuario));
        if (fechaDesde != null || fechaHasta != null)
            spec = spec.and(PedidoSpecification.fechaBetween(fechaDesde, fechaHasta));
        if (montoMin != null || montoMax != null)
            spec = spec.and(PedidoSpecification.montoTotalBetween(montoMin, montoMax));

        return pedidoRepository.findAll(spec, pageable).map(pedidoMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoDTO> filtrarMisPedidos(String correoUsuario, EstadoPedido estado, Pageable pageable) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Specification<Pedido> spec = Specification.where((Specification<Pedido>) PedidoSpecification.hasUsuario(usuario.getIdUsuario()));

        if (estado != null)
            spec = spec.and(PedidoSpecification.hasEstado(estado));

        return pedidoRepository.findAll(spec, pageable).map(pedidoMapper::toDTO);
    }
}