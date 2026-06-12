package com.meowtfit.backend.facturacion.mapper;

import com.meowtfit.backend.facturacion.dto.ComprobantePagoDTO;
import com.meowtfit.backend.facturacion.entity.ComprobantePago;
import org.springframework.stereotype.Component;

@Component
public class ComprobantePagoMapper {

    public ComprobantePagoDTO toDTO(ComprobantePago entity) {
        if (entity == null) return null;
        ComprobantePagoDTO dto = new ComprobantePagoDTO();
        dto.setIdComprobante(entity.getIdComprobante());
        dto.setArchivo(entity.getArchivo());
        dto.setFechaSubida(entity.getFechaSubida());
        dto.setTipoArchivo(entity.getTipoArchivo());
        if (entity.getPedido() != null) {
            dto.setIdPedido(entity.getPedido().getIdPedido());
        }
        return dto;
    }
}
