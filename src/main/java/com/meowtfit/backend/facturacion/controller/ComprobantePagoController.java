package com.meowtfit.backend.facturacion.controller;

import com.meowtfit.backend.facturacion.dto.ComprobantePagoDTO;
import com.meowtfit.backend.facturacion.service.ComprobantePagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/comprobantes-pago")
@RequiredArgsConstructor
public class ComprobantePagoController {

    private final ComprobantePagoService comprobantePagoService;

    @PostMapping(value = "/{idPedido}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComprobantePagoDTO> subirComprobante(
            @PathVariable Long idPedido,
            @RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comprobantePagoService.subirComprobante(idPedido, archivo));
    }

    @GetMapping("/{idComprobante}")
    public ResponseEntity<ComprobantePagoDTO> obtenerPorId(@PathVariable Long idComprobante) {
        return ResponseEntity.ok(comprobantePagoService.obtenerPorId(idComprobante));
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<ComprobantePagoDTO> obtenerPorPedido(@PathVariable Long idPedido) {
        return ResponseEntity.ok(comprobantePagoService.obtenerPorPedido(idPedido));
    }

    @GetMapping("/{idComprobante}/archivo")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable Long idComprobante) {
        ComprobantePagoDTO dto = comprobantePagoService.obtenerPorId(idComprobante);
        Resource recurso = comprobantePagoService.descargarArchivo(idComprobante);

        String contentType = dto.getTipoArchivo() != null
                ? dto.getTipoArchivo()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + dto.getArchivo() + "\"")
                .body(recurso);
    }

    @DeleteMapping("/{idComprobante}")
    public ResponseEntity<Void> eliminarComprobante(@PathVariable Long idComprobante) {
        comprobantePagoService.eliminarComprobante(idComprobante);
        return ResponseEntity.noContent().build();
    }
}
