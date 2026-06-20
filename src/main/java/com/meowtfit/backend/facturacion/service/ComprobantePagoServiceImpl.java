package com.meowtfit.backend.facturacion.service;

import com.meowtfit.backend.exception.BadRequestException;
import com.meowtfit.backend.exception.ResourceNotFoundException;
import com.meowtfit.backend.facturacion.dto.ComprobantePagoDTO;
import com.meowtfit.backend.facturacion.entity.ComprobantePago;
import com.meowtfit.backend.facturacion.mapper.ComprobantePagoMapper;
import com.meowtfit.backend.facturacion.repository.ComprobantePagoRepository;
import com.meowtfit.backend.pedido.entity.Pedido;
import com.meowtfit.backend.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComprobantePagoServiceImpl implements ComprobantePagoService {

    private final ComprobantePagoRepository comprobantePagoRepository;
    private final PedidoRepository pedidoRepository;
    private final ComprobantePagoMapper comprobantePagoMapper;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    private static final long MAX_TAMANIO_BYTES = 10 * 1024 * 1024; // 10 MB
    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
        "image/jpeg", "image/png", "image/jpg", "application/pdf"
    );

    @Override
    @Transactional
    public ComprobantePagoDTO subirComprobante(Long idPedido, MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BadRequestException("El archivo del comprobante no puede estar vacío");
        }
        if (archivo.getSize() > MAX_TAMANIO_BYTES) {
            throw new BadRequestException("El archivo supera el tamaño máximo permitido de 10 MB");
        }
        String contentType = archivo.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType)) {
            throw new BadRequestException("Tipo de archivo no permitido. Se aceptan: JPG, PNG, PDF");
        }

        Pedido pedido = pedidoRepository.findById(idPedido)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + idPedido));

        // Si ya existe un comprobante para este pedido, eliminar el anterior de S3
        Optional<ComprobantePago> existente = comprobantePagoRepository.findByPedidoIdPedido(idPedido);
        existente.ifPresent(c -> {
            eliminarArchivoS3(c.getArchivo());
            comprobantePagoRepository.delete(c);
        });

        String s3Key = subirArchivoS3(archivo, contentType);

        ComprobantePago comprobante = new ComprobantePago();
        comprobante.setArchivo(s3Key);
        comprobante.setTipoArchivo(contentType);
        comprobante.setPedido(pedido);

        return comprobantePagoMapper.toDTO(comprobantePagoRepository.save(comprobante));
    }

    @Override
    public ComprobantePagoDTO obtenerPorId(Long idComprobante) {
        ComprobantePago comprobante = comprobantePagoRepository.findById(idComprobante)
            .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado con id: " + idComprobante));
        return comprobantePagoMapper.toDTO(comprobante);
    }

    @Override
    public ComprobantePagoDTO obtenerPorPedido(Long idPedido) {
        ComprobantePago comprobante = comprobantePagoRepository.findByPedidoIdPedido(idPedido)
            .orElseThrow(() -> new ResourceNotFoundException("No existe comprobante para el pedido id: " + idPedido));
        return comprobantePagoMapper.toDTO(comprobante);
    }

    @Override
    public Resource descargarArchivo(Long idComprobante) {
        ComprobantePago comprobante = comprobantePagoRepository.findById(idComprobante)
            .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado con id: " + idComprobante));

        var request = GetObjectRequest.builder()
            .bucket(bucket)
            .key(comprobante.getArchivo())
            .build();

        return new InputStreamResource(s3Client.getObject(request));
    }

    @Override
    @Transactional
    public void eliminarComprobante(Long idComprobante) {
        ComprobantePago comprobante = comprobantePagoRepository.findById(idComprobante)
            .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado con id: " + idComprobante));
        eliminarArchivoS3(comprobante.getArchivo());
        comprobantePagoRepository.delete(comprobante);
    }

    private String subirArchivoS3(MultipartFile archivo, String contentType) {
        String extension = StringUtils.getFilenameExtension(archivo.getOriginalFilename());
        String key = "comprobantes/" + UUID.randomUUID() + (extension != null ? "." + extension : "");
        try {
            var request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .contentLength(archivo.getSize())
                .build();
            s3Client.putObject(request, RequestBody.fromInputStream(archivo.getInputStream(), archivo.getSize()));
            return key;
        } catch (IOException e) {
            throw new BadRequestException("Error al subir el archivo a S3: " + e.getMessage());
        }
    }

    private void eliminarArchivoS3(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
        } catch (Exception ignored) {
        }
    }
}
