package com.meowtfit.backend.cotizacion.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.meowtfit.backend.cotizacion.dto.CotizacionDTO;
import com.meowtfit.backend.cotizacion.dto.LineaCotizacionDTO;
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
        dto.setFechaCreacion(cotizacion.getFechaCreacion());
        dto.setEstado(cotizacion.getEstado());
        dto.setPrecioSugerido(cotizacion.getPrecioSugerido());
        dto.setSustento(cotizacion.getSustento());
        dto.setMontoSugerido(cotizacion.getMontoSugerido());
        dto.setMontoReal(cotizacion.getMontoReal());

        // Relaciones
        Usuario cliente = cotizacion.getCliente();
        if (cliente != null) {
            dto.setIdCliente(cliente.getIdUsuario());
            dto.setNombreCliente(cliente.getNombres());
        }

        Usuario comerciante = cotizacion.getComerciante();
        if (comerciante != null) {
            dto.setIdComerciante(comerciante.getIdUsuario());
            dto.setNombreComerciante(comerciante.getNombres());
        }

        if (cotizacion.getProducto() != null) {
            dto.setIdProducto(cotizacion.getProducto().getIdProducto());
            dto.setNombreProducto(cotizacion.getProducto().getNombre());
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
        dto.setSustento(contra.getSustento());
        dto.setFechaCreacion(contra.getFechaCreacion());
        dto.setPrecioNuevo(contra.getPrecioNuevo());
        dto.setIdCotizacion(contra.getCotizacion() != null ? contra.getCotizacion().getIdCotizacion() : null);
        
        if (contra.getUserGenerador() != null) {
            dto.setIdUserGenerador(contra.getUserGenerador().getIdUsuario());
            dto.setNombreUserGenerador(contra.getUserGenerador().getNombres());
        }
        
        return dto;
    }

    // ==================== TO ENTITY (desde RequestDTO) ====================

    public Cotizacion toEntity(CotizacionRequestDTO request) {
        if (request == null) return null;

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setPrecioSugerido(request.getPrecioSugerido());
        cotizacion.setSustento(request.getSustento());
        
        return cotizacion;
    }

    public LineaCotizacion toEntity(LineaCotizacionRequestDTO request) {
        if (request == null) return null;

        LineaCotizacion linea = new LineaCotizacion();
        linea.setCantidad(request.getCantidad());
        return linea;
    }

    public Contrapropuesta toEntity(ContrapropuestaRequestDTO request) {
        if (request == null) return null;

        Contrapropuesta contra = new Contrapropuesta();
        contra.setPrecioNuevo(request.getPrecioNuevo());
        contra.setSustento(request.getSustento());
        return contra;
    }
}