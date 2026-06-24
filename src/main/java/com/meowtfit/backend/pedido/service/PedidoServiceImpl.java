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
import com.meowtfit.backend.common.service.EmailService;
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
    private final EmailService emailService;

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
    public PedidoDTO cambiarEstado(Long idPedido, EstadoPedido nuevoEstado, String motivoRechazo) {
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
        PedidoDTO pedidoActualizado = pedidoMapper.toDTO(pedidoRepository.save(pedido));

        // Obtener datos del cliente para enviar el correo
        String correoCliente = pedido.getUsuario().getCorreo();
        String nombreCliente = pedido.getUsuario().getNombres();
        String codigoFormateado = "F" + String.format("%03d", idPedido);

        if (nuevoEstado == EstadoPedido.CONFIRMADO) {
            String asunto = "¡Pago Confirmado! - Pedido " + codigoFormateado;
            String cuerpoHTML = 
                "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e4e4e7; border-radius: 12px; padding: 24px; color: #18181b;\">"
                + "  <div style=\"text-align: center; border-bottom: 1px solid #e4e4e7; padding-bottom: 16px; margin-bottom: 24px;\">"
                + "    <h2 style=\"color: #087f99; margin: 0; font-size: 24px;\">MEOWTFIT</h2>"
                + "    <p style=\"color: #71717a; margin: 4px 0 0 0; font-size: 12px;\">¡Tu estilo felino favorito está en camino!</p>"
                + "  </div>"
                + "  <p style=\"font-size: 16px; margin-top: 0;\">Hola <strong>" + nombreCliente + "</strong>,</p>"
                + "  <p style=\"font-size: 14px; line-height: 1.6; color: #3f3f46;\">"
                + "    Te informamos que hemos validado con éxito tu comprobante de pago para el <strong>Pedido " + codigoFormateado + "</strong>. "
                + "    Su estado ha cambiado a <strong>CONFIRMADO</strong> y ya estamos preparando tus prendas para el envío."
                + "  </p>"
                + "  <div style=\"background-color: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 8px; padding: 16px; margin: 24px 0; text-align: center;\">"
                + "    <span style=\"font-size: 14px; color: #166534; font-weight: bold; display: block; margin-bottom: 4px;\">Monto total validado (incl. IGV)</span>"
                + "    <span style=\"font-size: 20px; color: #15803d; font-weight: 800;\">S/ " + pedido.getMontoTotal().setScale(2) + "</span>"
                + "  </div>"
                + "  <p style=\"font-size: 14px; line-height: 1.6; color: #3f3f46;\">"
                + "    Te mantendremos al tanto del estado de tu pedido cuando sea despachado y enviado a tu dirección."
                + "  </p>"
                + "  <p style=\"font-size: 11px; margin-top: 32px; color: #71717a; text-align: center; border-top: 1px solid #e4e4e7; padding-top: 16px;\">"
                + "    Este es un correo automático, por favor no respondas a este mensaje.<br>"
                + "    Meowtfit"
                + "  </p>"
                + "</div>";

            emailService.sendEmail(correoCliente, asunto, cuerpoHTML);

        } else if (nuevoEstado == EstadoPedido.PAGO_RECHAZADO) {
            String asunto = "Acción Requerida: Pago Rechazado - Pedido " + codigoFormateado;
            String motivoDisplay = (motivoRechazo != null && !motivoRechazo.isBlank()) ? motivoRechazo : "El comprobante no corresponde al monto o no es legible.";
            
            String cuerpoHTML = 
                "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e4e4e7; border-radius: 12px; padding: 24px; color: #18181b;\">"
                + "  <div style=\"text-align: center; border-bottom: 1px solid #e4e4e7; padding-bottom: 16px; margin-bottom: 24px;\">"
                + "    <h2 style=\"color: #087f99; margin: 0; font-size: 24px;\">MEOWTFIT</h2>"
                + "    <p style=\"color: #71717a; margin: 4px 0 0 0; font-size: 12px;\">Información sobre tu validación de pago</p>"
                + "  </div>"
                + "  <p style=\"font-size: 16px; margin-top: 0;\">Hola <strong>" + nombreCliente + "</strong>,</p>"
                + "  <p style=\"font-size: 14px; line-height: 1.6; color: #3f3f46;\">"
                + "    Lamentamos informarte que el comprobante de pago enviado para el <strong>Pedido " + codigoFormateado + "</strong> ha sido <strong>RECHAZADO</strong> por nuestro equipo."
                + "  </p>"
                + "  <div style=\"background-color: #fff1f2; border: 1px solid #fecdd3; border-radius: 8px; padding: 16px; margin: 24px 0;\">"
                + "    <span style=\"font-size: 13px; color: #9f1239; font-weight: bold; display: block; margin-bottom: 6px;\">Motivo del rechazo:</span>"
                + "    <p style=\"font-size: 14px; color: #be123c; margin: 0; font-style: italic; font-family: Georgia, serif;\">"
                + "      \"" + motivoDisplay + "\""
                + "    </p>"
                + "  </div>"
                + "  <p style=\"font-size: 14px; line-height: 1.6; color: #3f3f46;\">"
                + "    Para continuar con tu compra, por favor ingresa a tu perfil en nuestra tienda virtual, ve al detalle de tu pedido y sube un nuevo comprobante de pago válido."
                + "  </p>"
                + "  <p style=\"font-size: 11px; margin-top: 32px; color: #71717a; text-align: center; border-top: 1px solid #e4e4e7; padding-top: 16px;\">"
                + "    Este es un correo automático, por favor no respondas a este mensaje.<br>"
                + "    Meowtfit"
                + "  </p>"
                + "</div>";

            emailService.sendEmail(correoCliente, asunto, cuerpoHTML);
        }

        return pedidoActualizado;
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