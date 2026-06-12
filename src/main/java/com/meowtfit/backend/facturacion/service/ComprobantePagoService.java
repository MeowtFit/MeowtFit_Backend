package com.meowtfit.backend.facturacion.service;

import com.meowtfit.backend.facturacion.dto.ComprobantePagoDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ComprobantePagoService {
    ComprobantePagoDTO subirComprobante(Long idPedido, MultipartFile archivo);
    ComprobantePagoDTO obtenerPorId(Long idComprobante);
    ComprobantePagoDTO obtenerPorPedido(Long idPedido);
    Resource descargarArchivo(Long idComprobante);
    void eliminarComprobante(Long idComprobante);
}
