package com.meowtfit.backend.facturacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantePagoDTO {
    private Long idComprobante;
    private String archivo;
    private LocalDateTime fechaSubida;
    private String tipoArchivo;
    private Long idPedido;
}
