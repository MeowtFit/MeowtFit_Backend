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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComprobantePagoServiceImpl implements ComprobantePagoService {

    private final ComprobantePagoRepository comprobantePagoRepository;
    private final PedidoRepository pedidoRepository;
    private final ComprobantePagoMapper comprobantePagoMapper;

    @Value("${app.upload.comprobantes-dir:uploads/comprobantes}")
    private String uploadDir;

    private static final long MAX_TAMANIO_BYTES = 10 * 1024 * 1024; // 10 MB
    private static final java.util.Set<String> TIPOS_PERMITIDOS = java.util.Set.of(
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

        // Si ya existe un comprobante para este pedido, reemplazarlo
        Optional<ComprobantePago> existente = comprobantePagoRepository.findByPedidoIdPedido(idPedido);
        existente.ifPresent(c -> {
            eliminarArchivoFisico(c.getArchivo());
            comprobantePagoRepository.delete(c);
        });

        String nombreArchivo = guardarArchivoEnDisco(archivo);

        ComprobantePago comprobante = new ComprobantePago();
        comprobante.setArchivo(nombreArchivo);
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
        try {
            Path rutaArchivo = Paths.get(uploadDir).resolve(comprobante.getArchivo()).normalize();
            Resource recurso = new UrlResource(rutaArchivo.toUri());
            if (!recurso.exists() || !recurso.isReadable()) {
                throw new ResourceNotFoundException("No se pudo leer el archivo del comprobante");
            }
            return recurso;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("No se pudo leer el archivo del comprobante");
        }
    }

    @Override
    @Transactional
    public void eliminarComprobante(Long idComprobante) {
        ComprobantePago comprobante = comprobantePagoRepository.findById(idComprobante)
            .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado con id: " + idComprobante));
        eliminarArchivoFisico(comprobante.getArchivo());
        comprobantePagoRepository.delete(comprobante);
    }

    private String guardarArchivoEnDisco(MultipartFile archivo) {
        String extension = StringUtils.getFilenameExtension(archivo.getOriginalFilename());
        String nombreUnico = UUID.randomUUID().toString() + (extension != null ? "." + extension : "");
        try {
            Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dirPath);
            Path destino = dirPath.resolve(nombreUnico);
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return nombreUnico;
        } catch (IOException e) {
            throw new BadRequestException("Error al guardar el archivo: " + e.getMessage());
        }
    }

    private void eliminarArchivoFisico(String nombreArchivo) {
        try {
            Path rutaArchivo = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(nombreArchivo);
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException ignored) {
        }
    }
}
