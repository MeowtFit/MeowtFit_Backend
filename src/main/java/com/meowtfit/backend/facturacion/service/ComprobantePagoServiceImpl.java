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
import org.springframework.core.io.UrlResource;
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
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ComprobantePagoServiceImpl implements ComprobantePagoService {

    private final ComprobantePagoRepository comprobantePagoRepository;
    private final PedidoRepository pedidoRepository;
    private final ComprobantePagoMapper comprobantePagoMapper;
    private final Optional<S3Client> s3Client;

    @Value("${aws.s3.bucket:meowtfit-imagenes}")
    private String bucket;

    @Value("${app.upload.comprobantes-dir:uploads/comprobantes}")
    private String uploadDir;

    private static final long MAX_TAMANIO_BYTES = 10 * 1024 * 1024;
    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
        "image/jpeg", "image/png", "image/jpg", "application/pdf"
    );

    public ComprobantePagoServiceImpl(
            ComprobantePagoRepository comprobantePagoRepository,
            PedidoRepository pedidoRepository,
            ComprobantePagoMapper comprobantePagoMapper,
            Optional<S3Client> s3Client) {
        this.comprobantePagoRepository = comprobantePagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.comprobantePagoMapper = comprobantePagoMapper;
        this.s3Client = s3Client;
    }

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

        Optional<ComprobantePago> existente = comprobantePagoRepository.findByPedidoIdPedido(idPedido);
        existente.ifPresent(c -> {
            eliminarArchivo(c.getArchivo());
            comprobantePagoRepository.delete(c);
        });

        String referencia = s3Client.isPresent()
            ? subirArchivoS3(archivo, contentType)
            : guardarArchivoEnDisco(archivo);

        ComprobantePago comprobante = new ComprobantePago();
        comprobante.setArchivo(referencia);
        comprobante.setTipoArchivo(contentType);
        comprobante.setPedido(pedido);

        return comprobantePagoMapper.toDTO(comprobantePagoRepository.save(comprobante));
    }

    @Override
    public ComprobantePagoDTO obtenerPorId(Long idComprobante) {
        return comprobantePagoMapper.toDTO(buscarPorId(idComprobante));
    }

    @Override
    public ComprobantePagoDTO obtenerPorPedido(Long idPedido) {
        ComprobantePago comprobante = comprobantePagoRepository.findByPedidoIdPedido(idPedido)
            .orElseThrow(() -> new ResourceNotFoundException("No existe comprobante para el pedido id: " + idPedido));
        return comprobantePagoMapper.toDTO(comprobante);
    }

    @Override
    public Resource descargarArchivo(Long idComprobante) {
        ComprobantePago comprobante = buscarPorId(idComprobante);
        return s3Client.isPresent()
            ? descargarDeS3(comprobante.getArchivo())
            : descargarDeDisco(comprobante.getArchivo());
    }

    @Override
    @Transactional
    public void eliminarComprobante(Long idComprobante) {
        ComprobantePago comprobante = buscarPorId(idComprobante);
        eliminarArchivo(comprobante.getArchivo());
        comprobantePagoRepository.delete(comprobante);
    }

    // ── helpers S3 ────────────────────────────────────────────

    private String subirArchivoS3(MultipartFile archivo, String contentType) {
        String extension = StringUtils.getFilenameExtension(archivo.getOriginalFilename());
        String key = "comprobantes/" + UUID.randomUUID() + (extension != null ? "." + extension : "");
        try {
            s3Client.get().putObject(
                PutObjectRequest.builder()
                    .bucket(bucket).key(key)
                    .contentType(contentType)
                    .contentLength(archivo.getSize())
                    .build(),
                RequestBody.fromInputStream(archivo.getInputStream(), archivo.getSize())
            );
            return key;
        } catch (IOException e) {
            throw new BadRequestException("Error al subir el archivo a S3: " + e.getMessage());
        }
    }

    private Resource descargarDeS3(String key) {
        return new InputStreamResource(s3Client.get().getObject(
            GetObjectRequest.builder().bucket(bucket).key(key).build()
        ));
    }

    // ── helpers disco local ───────────────────────────────────

    private String guardarArchivoEnDisco(MultipartFile archivo) {
        String extension = StringUtils.getFilenameExtension(archivo.getOriginalFilename());
        String nombre = UUID.randomUUID() + (extension != null ? "." + extension : "");
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Files.copy(archivo.getInputStream(), dir.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
            return nombre;
        } catch (IOException e) {
            throw new BadRequestException("Error al guardar el archivo: " + e.getMessage());
        }
    }

    private Resource descargarDeDisco(String nombre) {
        try {
            Path ruta = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(nombre);
            Resource recurso = new UrlResource(ruta.toUri());
            if (!recurso.exists() || !recurso.isReadable()) {
                throw new ResourceNotFoundException("No se pudo leer el archivo del comprobante");
            }
            return recurso;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("No se pudo leer el archivo del comprobante");
        }
    }

    // ── helper compartido ─────────────────────────────────────

    private void eliminarArchivo(String referencia) {
        if (s3Client.isPresent()) {
            try {
                s3Client.get().deleteObject(
                    DeleteObjectRequest.builder().bucket(bucket).key(referencia).build()
                );
            } catch (Exception ignored) {}
        } else {
            try {
                Files.deleteIfExists(
                    Paths.get(uploadDir).toAbsolutePath().normalize().resolve(referencia)
                );
            } catch (IOException ignored) {}
        }
    }

    private ComprobantePago buscarPorId(Long idComprobante) {
        return comprobantePagoRepository.findById(idComprobante)
            .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado con id: " + idComprobante));
    }
}
