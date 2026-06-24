package com.meowtfit.backend.cotizacion.mapper;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.meowtfit.backend.cotizacion.dto.CotizacionDTO;
import com.meowtfit.backend.cotizacion.dto.LineaCotizacionDTO;
import com.meowtfit.backend.cotizacion.entity.EstadoCotizacion;
import com.meowtfit.backend.cotizacion.dto.ContrapropuestaDTO;
import com.meowtfit.backend.cotizacion.dto.CotizacionRequestDTO;
import com.meowtfit.backend.cotizacion.dto.LineaCotizacionRequestDTO;
import com.meowtfit.backend.cotizacion.dto.ContrapropuestaRequestDTO;
import com.meowtfit.backend.cotizacion.entity.Cotizacion;
import com.meowtfit.backend.cotizacion.entity.LineaCotizacion;
import com.meowtfit.backend.cotizacion.entity.Contrapropuesta;
import com.meowtfit.backend.color.entity.Color;
import com.meowtfit.backend.catalogo.entity.VarianteProducto;
import com.meowtfit.backend.usuario.entity.Usuario;

@Component
public class CotizacionMapper {

    // ==================== TO DTO ====================

    public CotizacionDTO toDTO(Cotizacion cotizacion) {
        if (cotizacion == null) return null;

        CotizacionDTO dto = new CotizacionDTO();
        dto.setIdCotizacion(cotizacion.getIdCotizacion());
        dto.setFecha(cotizacion.getFecha());
        dto.setEstado(cotizacion.getEstado());
        dto.setPrecioPresupuesto(cotizacion.getPrecioPresupuesto());
        dto.setComentario(cotizacion.getComentario());
        dto.setMontoTotal(cotizacion.getMontoTotal());
        dto.setDescuento(cotizacion.getDescuento());

        // Relaciones
        Usuario usuario = cotizacion.getUsuario();
        if (usuario != null) {
            dto.setIdUsuario(usuario.getIdUsuario());
            dto.setNombreUsuario(usuario.getNombres());
        }

        if (cotizacion.getReglaDescuento() != null) {
            dto.setIdRegla(cotizacion.getReglaDescuento().getIdRegla());
        }

        if (cotizacion.getPedido() != null) {
            dto.setIdPedido(cotizacion.getPedido().getIdPedido());
        }

        // Líneas
        if (cotizacion.getLineas() != null) {
            dto.setLineas(cotizacion.getLineas().stream()
                    .map(this::toLineaDTO)
                    .collect(Collectors.toList()));
        }

        // Contrapropuestas
        if (cotizacion.getContrapropuestas() != null) {
            dto.setContrapropuestas(cotizacion.getContrapropuestas().stream()
                    .map(this::toContrapropuestaDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private LineaCotizacionDTO toLineaDTO(LineaCotizacion linea) {
        LineaCotizacionDTO dto = new LineaCotizacionDTO();
        dto.setIdLineaCotizacion(linea.getIdLineaCotizacion());
        dto.setCantidad(linea.getCantidad());
        dto.setPrecioUnitario(linea.getPrecioUnitario());
        dto.setSubtotal(linea.getSubtotal());
        dto.setDescuentoAplicado(linea.getDescuentoAplicado());
        dto.setStockDestinado(linea.getStockDestinado());

        VarianteProducto variante = linea.getVarianteProducto();
        if (variante != null) {
            dto.setIdVariante(variante.getIdVariante());
            dto.setTalla(variante.getTalla());

            Color color = variante.getColor();
            if (color != null) {
                dto.setColor(color.getNombre());
                dto.setColorHex(color.getHexadecimal());
            }

            if (variante.getProducto() != null) {
                dto.setNombreProducto(variante.getProducto().getNombre());
            }
        }
        return dto;
    }

    private ContrapropuestaDTO toContrapropuestaDTO(Contrapropuesta contra) {
        ContrapropuestaDTO dto = new ContrapropuestaDTO();
        dto.setIdContrapropuesta(contra.getIdContrapropuesta());
        dto.setComentario(contra.getComentario());
        dto.setFecha(contra.getFecha());
        dto.setPrecioNuevo(contra.getPrecioNuevo());
        dto.setIdCotizacion(contra.getCotizacion() != null ? contra.getCotizacion().getIdCotizacion() : null);
        return dto;
    }

    // ==================== TO ENTITY (desde RequestDTO) ====================

    public Cotizacion toEntity(CotizacionRequestDTO request) {
        if (request == null) return null;

        Cotizacion cotizacion = new Cotizacion();
        // id, fecha, estado se generan automáticamente
        cotizacion.setComentario(request.getComentario());
        cotizacion.setDescuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO);
        // Las relaciones (usuario, regla, pedido) se deben setear externamente en el servicio
        // porque necesitamos cargar las entidades desde la BD
        return cotizacion;
    }

    public LineaCotizacion toEntity(LineaCotizacionRequestDTO request) {
        if (request == null) return null;

        LineaCotizacion linea = new LineaCotizacion();
        linea.setCantidad(request.getCantidad());
        linea.setPrecioUnitario(request.getPrecioUnitario());
        linea.setDescuentoAplicado(request.getDescuentoAplicado() != null ? request.getDescuentoAplicado() : BigDecimal.ZERO);
        // El subtotal se calcula en el servicio (cantidad * precioUnitario - descuento)
        // stockDestinado inicia en 0
        // La variante y la cotización se setean externamente
        return linea;
    }

    public Contrapropuesta toEntity(ContrapropuestaRequestDTO request) {
        if (request == null) return null;

        Contrapropuesta contra = new Contrapropuesta();
        contra.setPrecioNuevo(request.getPrecioNuevo());
        contra.setComentario(request.getComentario());
        // La cotización se setea externamente
        return contra;
    }
}