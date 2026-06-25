package com.meowtfit.backend.cotizacion.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.meowtfit.backend.negocio.entity.ConfiguracionNegocio;
import com.meowtfit.backend.negocio.repository.ConfiguracionNegocioRepository;
import com.meowtfit.backend.exception.BadRequestException;
import com.meowtfit.backend.exception.ResourceNotFoundException;
import com.meowtfit.backend.catalogo.entity.Producto;
import com.meowtfit.backend.catalogo.repository.ProductoRepository;
import com.meowtfit.backend.catalogo.entity.VarianteProducto;
import com.meowtfit.backend.catalogo.repository.VarianteProductoRepository;
import com.meowtfit.backend.usuario.entity.Usuario;
import com.meowtfit.backend.usuario.repository.UsuarioRepository;
import com.meowtfit.backend.common.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final LineaCotizacionRepository lineaCotizacionRepository;
    private final ContrapropuestaRepository contrapropuestaRepository;
    private final ProductoRepository productoRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConfiguracionNegocioRepository configuracionNegocioRepository;
    private final CotizacionMapper cotizacionMapper;
    private final EmailService emailService;

    private static final int MAX_CONTRAPROPUESTAS = 5;

    @Override
    @Transactional
    public CotizacionDTO crearCotizacion(CotizacionRequestDTO request, String correoUsuario) {
        Usuario cliente = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (cliente.getRuc() == null) {
            throw new BadRequestException("Solo los clientes B2B pueden generar cotizaciones.");
        }

        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        ConfiguracionNegocio config = configuracionNegocioRepository.findById(1L)
                .orElse(new ConfiguracionNegocio()); 

        Cotizacion cotizacion = cotizacionMapper.toEntity(request);
        cotizacion.setCliente(cliente);
        cotizacion.setProducto(producto);
        cotizacion.setEstado(EstadoCotizacion.PENDIENTE);

        int cantidadTotal = 0;
        BigDecimal montoReal = BigDecimal.ZERO;

        for (LineaCotizacionRequestDTO lineaRequest : request.getLineas()) {
            VarianteProducto variante = varianteProductoRepository.findById(lineaRequest.getIdVariante())
                    .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada: " + lineaRequest.getIdVariante()));
            
            if (!variante.getProducto().getIdProducto().equals(producto.getIdProducto())) {
                throw new BadRequestException("La variante " + variante.getIdVariante() + " no pertenece al producto " + producto.getIdProducto());
            }

            LineaCotizacion linea = cotizacionMapper.toEntity(lineaRequest);
            linea.setVarianteProducto(variante);
            linea.setPrecioUnitario(producto.getPrecioBase());
            
            BigDecimal subtotal = producto.getPrecioBase().multiply(BigDecimal.valueOf(lineaRequest.getCantidad()));
            linea.setSubtotal(subtotal);

            cantidadTotal += lineaRequest.getCantidad();
            montoReal = montoReal.add(subtotal);

            cotizacion.addLinea(linea);
        }

        if (cantidadTotal < config.getStockMinimoCotizacion()) {
            throw new BadRequestException("La cantidad total de prendas debe ser al menos " + config.getStockMinimoCotizacion());
        }

        cotizacion.setMontoSugerido(request.getPrecioSugerido());
        cotizacion.setMontoReal(montoReal);

        Cotizacion saved = cotizacionRepository.save(cotizacion);
        
        // Enviar correo al cliente
        enviarCorreoCotizacion(saved, "Cotización Recibida", "Hemos recibido tu solicitud de cotización por el producto " + producto.getNombre() + " y está en estado PENDIENTE de revisión por nuestros comerciantes.");

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
    public Page<CotizacionDTO> filtrarCotizaciones(EstadoCotizacion estado, Long idCliente, Long idComerciante,
            LocalDateTime fechaDesde, LocalDateTime fechaHasta,
            BigDecimal montoMin, BigDecimal montoMax, Pageable pageable) {

        return cotizacionRepository.filtrarCotizaciones(estado, idCliente, idComerciante, fechaDesde, fechaHasta, montoMin, montoMax, pageable)
                .map(cotizacionMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CotizacionDTO> filtrarMisCotizaciones(String correoUsuario, EstadoCotizacion estado, Pageable pageable) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return cotizacionRepository.filtrarMisCotizaciones(usuario.getIdUsuario(), estado, pageable)
                .map(cotizacionMapper::toDTO);
    }

    @Override
    @Transactional
    public CotizacionDTO aceptarCotizacion(Long idCotizacion, String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
                
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada"));

        if (cotizacion.getEstado() == EstadoCotizacion.CERRADA || cotizacion.getEstado() == EstadoCotizacion.RECHAZADA) {
            throw new BadRequestException("La cotización ya está finalizada.");
        }

        boolean esComerciante = "COMERCIANTE".equals(usuario.getRol().name());
        
        if (esComerciante && cotizacion.getComerciante() == null) {
            cotizacion.setComerciante(usuario);
        }

        cotizacion.setEstado(EstadoCotizacion.CERRADA);
        Cotizacion saved = cotizacionRepository.save(cotizacion);
        
        String rolQueAcepta = esComerciante ? "comerciante" : "cliente";
        Usuario destinatario = esComerciante ? cotizacion.getCliente() : cotizacion.getComerciante();
        
        if (destinatario != null) {
            enviarCorreoCotizacion(saved, "Cotización Aceptada y Cerrada", "El " + rolQueAcepta + " ha ACEPTADO la propuesta. La cotización se encuentra CERRADA con éxito. Puedes proceder con la gestión.");
        }

        return cotizacionMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public CotizacionDTO rechazarCotizacion(Long idCotizacion, String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
                
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada"));

        if (cotizacion.getEstado() == EstadoCotizacion.CERRADA || cotizacion.getEstado() == EstadoCotizacion.RECHAZADA) {
            throw new BadRequestException("La cotización ya está finalizada.");
        }

        boolean esComerciante = "COMERCIANTE".equals(usuario.getRol().name());
        
        if (esComerciante && cotizacion.getComerciante() == null) {
            cotizacion.setComerciante(usuario);
        }

        cotizacion.setEstado(EstadoCotizacion.RECHAZADA);
        Cotizacion saved = cotizacionRepository.save(cotizacion);
        
        String rolQueRechaza = esComerciante ? "comerciante" : "cliente";
        Usuario destinatario = esComerciante ? cotizacion.getCliente() : cotizacion.getComerciante();

        if (destinatario != null) {
            enviarCorreoCotizacion(saved, "Cotización Rechazada", "El " + rolQueRechaza + " ha RECHAZADO la cotización. La negociación ha finalizado sin acuerdo.");
        }
        
        return cotizacionMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public CotizacionDTO crearContrapropuesta(ContrapropuestaRequestDTO request, String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Cotizacion cotizacion = cotizacionRepository.findById(request.getIdCotizacion())
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada"));

        if (cotizacion.getEstado() == EstadoCotizacion.CERRADA || cotizacion.getEstado() == EstadoCotizacion.RECHAZADA) {
            throw new BadRequestException("La cotización ya está finalizada y no acepta contrapropuestas.");
        }

        long count = contrapropuestaRepository.countByCotizacionIdCotizacion(request.getIdCotizacion());
        if (count >= MAX_CONTRAPROPUESTAS) {
            throw new BadRequestException("Se ha alcanzado el límite máximo de " + MAX_CONTRAPROPUESTAS + " contrapropuestas");
        }

        boolean esComerciante = "COMERCIANTE".equals(usuario.getRol().name());

        if (esComerciante && cotizacion.getComerciante() == null) {
            cotizacion.setComerciante(usuario);
        }

        Contrapropuesta contrapropuesta = cotizacionMapper.toEntity(request);
        contrapropuesta.setCotizacion(cotizacion);
        contrapropuesta.setUserGenerador(usuario);
        
        contrapropuesta = contrapropuestaRepository.save(contrapropuesta);

        cotizacion.setEstado(EstadoCotizacion.CONTRAPROPUESTA);
        cotizacion.addContrapropuesta(contrapropuesta);

        Cotizacion saved = cotizacionRepository.save(cotizacion);
        
        String rolQuePropone = esComerciante ? "comerciante" : "cliente";
        Usuario destinatario = esComerciante ? cotizacion.getCliente() : cotizacion.getComerciante();

        if (destinatario != null) {
            enviarCorreoCotizacion(saved, "Nueva Contrapropuesta", "El " + rolQuePropone + " ha enviado una nueva contrapropuesta por S/ " + contrapropuesta.getPrecioNuevo().setScale(2) + ". Ingresa al sistema para revisarla.");
        }

        return cotizacionMapper.toDTO(saved);
    }
    
    private void enviarCorreoCotizacion(Cotizacion cotizacion, String titulo, String mensaje) {
        String codigoFormateado = "C" + String.format("%04d", cotizacion.getIdCotizacion());
        
        String cuerpoHTML = 
            "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e4e4e7; border-radius: 12px; padding: 24px; color: #18181b;\">"
            + "  <div style=\"text-align: center; border-bottom: 1px solid #e4e4e7; padding-bottom: 16px; margin-bottom: 24px;\">"
            + "    <h2 style=\"color: #087f99; margin: 0; font-size: 24px;\">MEOWTFIT B2B</h2>"
            + "    <p style=\"color: #71717a; margin: 4px 0 0 0; font-size: 12px;\">Gestión de Cotizaciones</p>"
            + "  </div>"
            + "  <h3 style=\"color: #18181b; margin-top: 0;\">" + titulo + "</h3>"
            + "  <p style=\"font-size: 14px; line-height: 1.6; color: #3f3f46;\">"
            +      mensaje
            + "  </p>"
            + "  <div style=\"background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 16px; margin: 24px 0;\">"
            + "    <span style=\"font-size: 13px; color: #64748b; font-weight: bold; display: block; margin-bottom: 6px;\">Detalles de Cotización " + codigoFormateado + ":</span>"
            + "    <ul style=\"font-size: 14px; color: #334155; margin: 0; padding-left: 20px;\">"
            + "      <li><strong>Producto:</strong> " + cotizacion.getProducto().getNombre() + "</li>"
            + "      <li><strong>Estado:</strong> " + cotizacion.getEstado() + "</li>"
            + "    </ul>"
            + "  </div>"
            + "  <p style=\"font-size: 11px; margin-top: 32px; color: #71717a; text-align: center; border-top: 1px solid #e4e4e7; padding-top: 16px;\">"
            + "    Este es un correo automático, por favor no respondas a este mensaje.<br>"
            + "    Meowtfit B2B"
            + "  </p>"
            + "</div>";

        // Enviar a los involucrados correspondientes.
        // Se asume que el cliente SIEMPRE se debe enterar, y el comerciante (si existe) también.
        if (cotizacion.getCliente() != null && cotizacion.getCliente().getCorreo() != null) {
            emailService.sendEmail(cotizacion.getCliente().getCorreo(), "Cotización " + codigoFormateado + " - " + titulo, cuerpoHTML);
        }
        
        if (cotizacion.getComerciante() != null && cotizacion.getComerciante().getCorreo() != null) {
            emailService.sendEmail(cotizacion.getComerciante().getCorreo(), "Cotización " + codigoFormateado + " - " + titulo, cuerpoHTML);
        }
    }
}