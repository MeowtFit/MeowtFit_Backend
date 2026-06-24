package com.meowtfit.backend.cotizacion.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meowtfit.backend.cotizacion.dto.CotizacionDTO;
import com.meowtfit.backend.cotizacion.dto.CotizacionRequestDTO;
import com.meowtfit.backend.cotizacion.dto.ContrapropuestaRequestDTO;
import com.meowtfit.backend.cotizacion.dto.LineaCotizacionRequestDTO;
import com.meowtfit.backend.cotizacion.entity.Cotizacion;
import com.meowtfit.backend.cotizacion.entity.Contrapropuesta;
import com.meowtfit.backend.cotizacion.entity.EstadoCotizacion;
import com.meowtfit.backend.cotizacion.entity.LineaCotizacion;
import com.meowtfit.backend.cotizacion.mapper.CotizacionMapper;
import com.meowtfit.backend.cotizacion.repository.CotizacionRepository;
import com.meowtfit.backend.cotizacion.repository.ContrapropuestaRepository;
import com.meowtfit.backend.cotizacion.repository.LineaCotizacionRepository;
import com.meowtfit.backend.exception.BadRequestException;
import com.meowtfit.backend.exception.ResourceNotFoundException;
import com.meowtfit.backend.pedido.entity.EstadoPedido;
import com.meowtfit.backend.pedido.entity.LineaPedido;
import com.meowtfit.backend.pedido.entity.Pedido;
import com.meowtfit.backend.pedido.repository.PedidoRepository;
import com.meowtfit.backend.catalogo.entity.VarianteProducto;
import com.meowtfit.backend.catalogo.repository.VarianteProductoRepository;
import com.meowtfit.backend.catalogo.entity.ReglaDescuento;
import com.meowtfit.backend.catalogo.repository.ReglaDescuentoRepository;
import com.meowtfit.backend.usuario.entity.Usuario;
import com.meowtfit.backend.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final LineaCotizacionRepository lineaCotizacionRepository;
    private final ContrapropuestaRepository contrapropuestaRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final ReglaDescuentoRepository reglaDescuentoRepository;
    private final CotizacionMapper cotizacionMapper;

    private static final int MAX_CONTRAPROPUESTAS = 5;

    @Override
    @Transactional
    public CotizacionDTO crearCotizacionDesdePedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        // Validar que el pedido esté confirmado y tenga estado REGISTRADO o CONFIRMADO
        if (pedido.getEstado() != EstadoPedido.REGISTRADO && pedido.getEstado() != EstadoPedido.CONFIRMADO) {
            throw new BadRequestException("El pedido debe estar en estado REGISTRADO o CONFIRMADO para generar cotización");
        }

        Usuario usuario = pedido.getUsuario();

        // Crear cotización
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setUsuario(usuario);
        cotizacion.setEstado(EstadoCotizacion.PENDIENTE);
        cotizacion.setPedido(pedido);
        cotizacion.setComentario("Cotización generada automáticamente desde el pedido #" + idPedido);
        cotizacion.setDescuento(pedido.getDescuento() != null ? pedido.getDescuento() : BigDecimal.ZERO);

        // Calcular monto total y crear líneas
        BigDecimal montoTotal = BigDecimal.ZERO;

        for (LineaPedido lineaPedido : pedido.getLineas()) {
            LineaCotizacion linea = new LineaCotizacion();
            linea.setCantidad(lineaPedido.getCantidad());
            linea.setPrecioUnitario(lineaPedido.getPrecioUnitario());
            linea.setDescuentoAplicado(lineaPedido.getDescuentoAplicado());
            linea.setSubtotal(lineaPedido.getSubtotal());
            linea.setStockDestinado(0); // Inicialmente 0, se asigna después
            linea.setVarianteProducto(lineaPedido.getVariante());

            montoTotal = montoTotal.add(lineaPedido.getSubtotal());
            cotizacion.addLinea(linea);
        }

        // Aplicar descuento global
        if (cotizacion.getDescuento() != null && cotizacion.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
            montoTotal = montoTotal.subtract(cotizacion.getDescuento());
        }

        cotizacion.setMontoTotal(montoTotal);
        cotizacion.setPrecioPresupuesto(montoTotal); // Inicialmente igual al monto total

        Cotizacion saved = cotizacionRepository.save(cotizacion);
        return cotizacionMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public CotizacionDTO crearCotizacionManual(CotizacionRequestDTO request, String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Cotizacion cotizacion = cotizacionMapper.toEntity(request);
        cotizacion.setUsuario(usuario);
        cotizacion.setEstado(EstadoCotizacion.PENDIENTE);

        // Asignar regla de descuento si se proporciona
        if (request.getIdRegla() != null) {
            ReglaDescuento regla = reglaDescuentoRepository.findById(request.getIdRegla())
                    .orElseThrow(() -> new ResourceNotFoundException("Regla de descuento no encontrada"));
            cotizacion.setReglaDescuento(regla);
        }

        // Asignar pedido si se proporciona
        if (request.getIdPedido() != null) {
            Pedido pedido = pedidoRepository.findById(request.getIdPedido())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
            cotizacion.setPedido(pedido);
        }

        // Procesar líneas
        BigDecimal montoTotal = BigDecimal.ZERO;
        for (LineaCotizacionRequestDTO lineaRequest : request.getLineas()) {
            VarianteProducto variante = varianteProductoRepository.findById(lineaRequest.getIdVariante())
                    .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));

            LineaCotizacion linea = cotizacionMapper.toEntity(lineaRequest);
            linea.setVarianteProducto(variante);
            linea.setStockDestinado(0);

            // Calcular subtotal
            BigDecimal subtotal = lineaRequest.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(lineaRequest.getCantidad()));
            if (lineaRequest.getDescuentoAplicado() != null) {
                subtotal = subtotal.subtract(lineaRequest.getDescuentoAplicado());
            }
            linea.setSubtotal(subtotal);
            montoTotal = montoTotal.add(subtotal);

            cotizacion.addLinea(linea);
        }

        // Aplicar descuento global
        if (cotizacion.getDescuento() != null && cotizacion.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
            montoTotal = montoTotal.subtract(cotizacion.getDescuento());
        }

        cotizacion.setMontoTotal(montoTotal);
        cotizacion.setPrecioPresupuesto(montoTotal);

        Cotizacion saved = cotizacionRepository.save(cotizacion);
        return cotizacionMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CotizacionDTO obtenerPorId(Long idCotizacion) {
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada"));
        return cotizacionMapper.toDTO(cotizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CotizacionDTO> listarPorUsuario(String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return cotizacionRepository.findByUsuario(usuario)
                .stream().map(cotizacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CotizacionDTO> listarPorEstado(EstadoCotizacion estado) {
        return cotizacionRepository.findByEstado(estado)
                .stream().map(cotizacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CotizacionDTO> listarTodos() {
        return cotizacionRepository.findAll()
                .stream().map(cotizacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CotizacionDTO cambiarEstado(Long idCotizacion, EstadoCotizacion nuevoEstado, String comentario) {
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada"));

        // Validar transiciones de estado
        validarTransicionEstado(cotizacion.getEstado(), nuevoEstado);

        // Si se aprueba, asignar stock automáticamente
        if (nuevoEstado == EstadoCotizacion.APROBADA) {
            asignarStockInterno(cotizacion);
        }

        // Si se rechaza o cierra, liberar stock destinado
        if (nuevoEstado == EstadoCotizacion.RECHAZADA || nuevoEstado == EstadoCotizacion.CERRADA) {
            liberarStockDestinado(cotizacion);
        }

        cotizacion.setEstado(nuevoEstado);
        if (comentario != null && !comentario.isEmpty()) {
            cotizacion.setComentario(cotizacion.getComentario() + " | " + comentario);
        }

        return cotizacionMapper.toDTO(cotizacionRepository.save(cotizacion));
    }

    @Override
    @Transactional
    public CotizacionDTO asignarStock(Long idCotizacion) {
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada"));

        if (cotizacion.getEstado() != EstadoCotizacion.PENDIENTE && 
            cotizacion.getEstado() != EstadoCotizacion.EN_REVISION) {
            throw new BadRequestException("Solo se puede asignar stock a cotizaciones en estado PENDIENTE o EN_REVISION");
        }

        asignarStockInterno(cotizacion);
        cotizacion.setEstado(EstadoCotizacion.EN_REVISION);
        
        return cotizacionMapper.toDTO(cotizacionRepository.save(cotizacion));
    }

    private void asignarStockInterno(Cotizacion cotizacion) {
        for (LineaCotizacion linea : cotizacion.getLineas()) {
            VarianteProducto variante = linea.getVarianteProducto();
            int cantidadSolicitada = linea.getCantidad();
            int stockDisponible = variante.getStockDisponible();

            if (stockDisponible < cantidadSolicitada) {
                // Si no hay suficiente stock, asignar lo disponible y generar alerta
                linea.setStockDestinado(stockDisponible);
                variante.setStockDisponible(0);
                // Se podría generar una alerta de abastecimiento aquí
            } else {
                linea.setStockDestinado(cantidadSolicitada);
                variante.setStockDisponible(stockDisponible - cantidadSolicitada);
                variante.setStockReservado(variante.getStockReservado() + cantidadSolicitada);
            }
            varianteProductoRepository.save(variante);
            lineaCotizacionRepository.save(linea);
        }
    }

    private void liberarStockDestinado(Cotizacion cotizacion) {
        for (LineaCotizacion linea : cotizacion.getLineas()) {
            if (linea.getStockDestinado() > 0) {
                VarianteProducto variante = linea.getVarianteProducto();
                variante.setStockDisponible(variante.getStockDisponible() + linea.getStockDestinado());
                variante.setStockReservado(variante.getStockReservado() - linea.getStockDestinado());
                linea.setStockDestinado(0);
                varianteProductoRepository.save(variante);
                lineaCotizacionRepository.save(linea);
            }
        }
    }

    @Override
    @Transactional
    public CotizacionDTO crearContrapropuesta(ContrapropuestaRequestDTO request, String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Cotizacion cotizacion = cotizacionRepository.findById(request.getIdCotizacion())
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada"));

        // Validar límite de contrapropuestas
        long count = contrapropuestaRepository.countByCotizacionIdCotizacion(request.getIdCotizacion());
        if (count >= MAX_CONTRAPROPUESTAS) {
            throw new BadRequestException("Se ha alcanzado el límite máximo de " + MAX_CONTRAPROPUESTAS + " contrapropuestas");
        }

        // Validar estado de la cotización
        if (cotizacion.getEstado() != EstadoCotizacion.PENDIENTE && 
            cotizacion.getEstado() != EstadoCotizacion.EN_REVISION && 
            cotizacion.getEstado() != EstadoCotizacion.CONTRAPROPUESTA) {
            throw new BadRequestException("No se pueden hacer contrapropuestas en el estado actual");
        }

        Contrapropuesta contrapropuesta = cotizacionMapper.toEntity(request);
        contrapropuesta.setCotizacion(cotizacion);
        contrapropuesta = contrapropuestaRepository.save(contrapropuesta);

        // Actualizar el estado de la cotización y el precio presupuesto
        cotizacion.setEstado(EstadoCotizacion.CONTRAPROPUESTA);
        cotizacion.setPrecioPresupuesto(request.getPrecioNuevo());
        cotizacion.addContrapropuesta(contrapropuesta);

        return cotizacionMapper.toDTO(cotizacionRepository.save(cotizacion));
    }

    @Override
    @Transactional
    public CotizacionDTO aceptarContrapropuesta(Long idCotizacion) {
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada"));

        if (cotizacion.getEstado() != EstadoCotizacion.CONTRAPROPUESTA) {
            throw new BadRequestException("Solo se puede aceptar una contrapropuesta en estado CONTRAPROPUESTA");
        }

        // Obtener la última contrapropuesta
        Contrapropuesta ultima = contrapropuestaRepository
                .findFirstByCotizacionIdCotizacionOrderByFechaDesc(idCotizacion)
                .orElseThrow(() -> new ResourceNotFoundException("No hay contrapropuestas para aceptar"));

        // Actualizar el precio presupuesto con el de la contrapropuesta
        cotizacion.setPrecioPresupuesto(ultima.getPrecioNuevo());
        cotizacion.setEstado(EstadoCotizacion.APROBADA);

        // Asignar stock
        asignarStockInterno(cotizacion);

        return cotizacionMapper.toDTO(cotizacionRepository.save(cotizacion));
    }

    @Override
    @Transactional
    public CotizacionDTO rechazarContrapropuesta(Long idCotizacion, String motivo) {
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada"));

        if (cotizacion.getEstado() != EstadoCotizacion.CONTRAPROPUESTA) {
            throw new BadRequestException("Solo se puede rechazar una contrapropuesta en estado CONTRAPROPUESTA");
        }

        cotizacion.setEstado(EstadoCotizacion.RECHAZADA);
        if (motivo != null && !motivo.isEmpty()) {
            cotizacion.setComentario(cotizacion.getComentario() + " | Rechazada: " + motivo);
        }

        // Liberar stock destinado
        liberarStockDestinado(cotizacion);

        return cotizacionMapper.toDTO(cotizacionRepository.save(cotizacion));
    }

    @Override
    @Transactional(readOnly = true)
    public long contarContrapropuestas(Long idCotizacion) {
        return contrapropuestaRepository.countByCotizacionIdCotizacion(idCotizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CotizacionDTO> filtrarCotizaciones(EstadoCotizacion estado, Long idUsuario,
            LocalDateTime fechaDesde, LocalDateTime fechaHasta,
            BigDecimal montoMin, BigDecimal montoMax, Pageable pageable) {

        return cotizacionRepository.filtrarCotizaciones(estado, idUsuario, fechaDesde, fechaHasta, montoMin, montoMax, pageable)
                .map(cotizacionMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CotizacionDTO> filtrarMisCotizaciones(String correoUsuario, EstadoCotizacion estado, Pageable pageable) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Usar el método con JPQL
        return cotizacionRepository.filtrarMisCotizaciones(usuario.getIdUsuario(), estado, pageable)
                .map(cotizacionMapper::toDTO);
    }

    // Métodos privados de validación
    private void validarTransicionEstado(EstadoCotizacion actual, EstadoCotizacion nuevo) {
        // Lógica de transiciones permitidas
        switch (actual) {
            case PENDIENTE:
                if (nuevo != EstadoCotizacion.EN_REVISION && 
                    nuevo != EstadoCotizacion.RECHAZADA) {
                    throw new BadRequestException("Desde PENDIENTE solo se puede pasar a EN_REVISION o RECHAZADA");
                }
                break;
            case EN_REVISION:
                if (nuevo != EstadoCotizacion.APROBADA && 
                    nuevo != EstadoCotizacion.RECHAZADA && 
                    nuevo != EstadoCotizacion.CONTRAPROPUESTA) {
                    throw new BadRequestException("Desde EN_REVISION solo se puede pasar a APROBADA, RECHAZADA o CONTRAPROPUESTA");
                }
                break;
            case CONTRAPROPUESTA:
                if (nuevo != EstadoCotizacion.APROBADA && 
                    nuevo != EstadoCotizacion.RECHAZADA && 
                    nuevo != EstadoCotizacion.CONTRAPROPUESTA) {
                    throw new BadRequestException("Desde CONTRAPROPUESTA solo se puede pasar a APROBADA, RECHAZADA o nueva CONTRAPROPUESTA");
                }
                break;
            case APROBADA:
                if (nuevo != EstadoCotizacion.CERRADA) {
                    throw new BadRequestException("Desde APROBADA solo se puede pasar a CERRADA");
                }
                break;
            case RECHAZADA:
            case CERRADA:
                throw new BadRequestException("No se puede cambiar el estado de una cotización " + actual);
            default:
                throw new BadRequestException("Transición de estado no válida");
        }
    }
}