package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.ProductoDTO;
import com.meowtfit.backend.catalogo.dto.ProductoRequestDTO;
import com.meowtfit.backend.catalogo.entity.Categoria;
import com.meowtfit.backend.catalogo.entity.EstadoProducto;
import com.meowtfit.backend.catalogo.entity.Producto;
import com.meowtfit.backend.catalogo.entity.ReglaDescuento;
import com.meowtfit.backend.catalogo.entity.VarianteProducto;
import com.meowtfit.backend.catalogo.mapper.ProductoMapper;
import com.meowtfit.backend.catalogo.mapper.ReglaDescuentoMapper;
import com.meowtfit.backend.catalogo.mapper.VarianteProductoMapper;
import com.meowtfit.backend.catalogo.repository.CategoriaRepository;
import com.meowtfit.backend.catalogo.repository.ProductoRepository;
import com.meowtfit.backend.catalogo.repository.ProductoSpecification;
import com.meowtfit.backend.color.repository.ColorRepository;
import com.meowtfit.backend.exception.BadRequestException;
import com.meowtfit.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final CategoriaRepository categoriaRepository;
    private final VarianteProductoMapper varianteProductoMapper;
    private final ReglaDescuentoMapper reglaDescuentoMapper;
    private final ColorRepository colorRepository;
    private final Optional<S3Client> s3Client;

    @Value("${aws.s3.bucket:meowtfit-imagenes}")
    private String bucket;

    @Value("${app.upload.productos-dir:uploads/productos}")
    private String uploadDir;

    private static final long MAX_IMG_BYTES = 10 * 1024 * 1024;
    private static final Set<String> TIPOS_IMAGEN = Set.of("image/jpeg", "image/png", "image/jpg", "image/webp");

    @Override
    public Page<ProductoDTO> filtrarProductos(String nombre, Long idCategoria, BigDecimal precioMin, BigDecimal precioMax, String talla, Pageable pageable) {
        Specification<Producto> spec = null;

        if (nombre != null && !nombre.trim().isEmpty()) {
            spec = Specification.where(ProductoSpecification.hasNombre(nombre.trim()));
        }

        if (idCategoria != null) {
            Specification<Producto> catSpec = ProductoSpecification.hasCategoria(idCategoria);
            spec = (spec == null) ? Specification.where(catSpec) : spec.and(catSpec);
        }
        
        if (precioMin != null || precioMax != null) {
            Specification<Producto> precioSpec = ProductoSpecification.precioBetween(precioMin, precioMax);
            spec = (spec == null) ? Specification.where(precioSpec) : spec.and(precioSpec);
        }
        
        if (talla != null && !talla.trim().isEmpty()) {
            Specification<Producto> tallaSpec = ProductoSpecification.hasTallaDisponible(talla.trim());
            spec = (spec == null) ? Specification.where(tallaSpec) : spec.and(tallaSpec);
        }

        if (spec == null) {
            return productoRepository.findAll(pageable).map(productoMapper::toDTO);
        }
        return productoRepository.findAll(spec, pageable).map(productoMapper::toDTO);
    }

    @Override
    @Transactional
    public ProductoDTO registrarProducto(ProductoRequestDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del producto no puede estar vacío");
        }
        if (dto.getIdCategoria() == null) {
            throw new BadRequestException("El id de la categoría es obligatorio");
        }
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + dto.getIdCategoria()));

        Producto producto = productoMapper.toEntity(dto, categoria);
        if (producto.getEstado() == null) {
            producto.setEstado(EstadoProducto.ACTIVO);
        }

        if (dto.getVariantes() != null) {
            List<VarianteProducto> variantes = dto.getVariantes().stream()
                .map(v -> {
                    var color = colorRepository.findById(v.getIdColor())
                        .orElseThrow(() -> new ResourceNotFoundException("Color no encontrado con id: " + v.getIdColor()));
                    return varianteProductoMapper.toEntity(v, producto, color);
                })
                .collect(Collectors.toList());
            producto.setVariantes(variantes);
        }

        if (dto.getReglasDescuento() != null) {
            List<ReglaDescuento> reglas = dto.getReglasDescuento().stream()
                .map(r -> reglaDescuentoMapper.toEntity(r, producto))
                .collect(Collectors.toList());
            producto.setReglasDescuento(reglas);
        }

        Producto guardado = productoRepository.save(producto);
        return productoMapper.toDTO(guardado);
    }

    @Override
    @Transactional
    public ProductoDTO editarProducto(Long idProducto, ProductoRequestDTO dto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + idProducto));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del producto no puede estar vacío");
        }
        if (dto.getIdCategoria() == null) {
            throw new BadRequestException("El id de la categoría es obligatorio");
        }
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + dto.getIdCategoria()));

        producto.setNombre(dto.getNombre());
        producto.setPrecioBase(dto.getPrecioBase());
        producto.setEstado(dto.getEstado());
        producto.setDescripcion(dto.getDescripcion());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setCategoria(categoria);

        if (dto.getVariantes() != null) {
            producto.getVariantes().clear();
            dto.getVariantes().stream()
                .map(v -> {
                    var color = colorRepository.findById(v.getIdColor())
                        .orElseThrow(() -> new ResourceNotFoundException("Color no encontrado con id: " + v.getIdColor()));
                    return varianteProductoMapper.toEntity(v, producto, color);
                })
                .forEach(producto.getVariantes()::add);
        }

        if (dto.getReglasDescuento() != null) {
            producto.getReglasDescuento().clear();
            dto.getReglasDescuento().stream()
                .map(r -> reglaDescuentoMapper.toEntity(r, producto))
                .forEach(producto.getReglasDescuento()::add);
        }

        Producto guardado = productoRepository.save(producto);
        return productoMapper.toDTO(guardado);
    }

    @Override
    public ProductoDTO buscarProductoPorId(Long idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + idProducto));
        return productoMapper.toDTO(producto);
    }

    @Override
    @Transactional
    public ProductoDTO activarProducto(Long idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + idProducto));
        producto.setEstado(EstadoProducto.ACTIVO);
        Producto guardado = productoRepository.save(producto);
        return productoMapper.toDTO(guardado);
    }

    @Override
    @Transactional
    public ProductoDTO desactivarProducto(Long idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + idProducto));
        producto.setEstado(EstadoProducto.INACTIVO);
        Producto guardado = productoRepository.save(producto);
        return productoMapper.toDTO(guardado);
    }

    @Override
    public List<ProductoDTO> listarProductosPorCategoria(Long idCategoria) {
        return productoRepository.findAll(Specification.where(ProductoSpecification.hasCategoria(idCategoria)))
                .stream().map(productoMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> listarProductosPorNombre(String nombre) {
        return productoRepository.findAll(Specification.where(ProductoSpecification.hasNombre(nombre)))
                .stream().map(productoMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String subirImagenProducto(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty())
            throw new BadRequestException("La imagen no puede estar vacía");
        if (archivo.getSize() > MAX_IMG_BYTES)
            throw new BadRequestException("La imagen supera el máximo de 10 MB");
        String contentType = archivo.getContentType();
        if (contentType == null || !TIPOS_IMAGEN.contains(contentType))
            throw new BadRequestException("Tipo no permitido. Se aceptan: JPG, PNG, WEBP");

        String extension = StringUtils.getFilenameExtension(archivo.getOriginalFilename());
        String filename = UUID.randomUUID() + (extension != null ? "." + extension : "");

        if (s3Client.isPresent()) {
            String key = "productos/" + filename;
            try {
                s3Client.get().putObject(
                    PutObjectRequest.builder()
                        .bucket(bucket).key(key)
                        .contentType(contentType)
                        .contentLength(archivo.getSize())
                        .build(),
                    RequestBody.fromInputStream(archivo.getInputStream(), archivo.getSize())
                );
            } catch (IOException e) {
                throw new BadRequestException("Error al subir la imagen a S3: " + e.getMessage());
            }
        } else {
            try {
                Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
                Files.createDirectories(dir);
                Files.copy(archivo.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new BadRequestException("Error al guardar la imagen: " + e.getMessage());
            }
        }

        return "/api/productos/imagen/" + filename;
    }

    @Override
    public Resource servirImagenProducto(String filename) {
        if (s3Client.isPresent()) {
            String key = "productos/" + filename;
            return new InputStreamResource(s3Client.get().getObject(
                GetObjectRequest.builder().bucket(bucket).key(key).build()
            ));
        }
        try {
            Path ruta = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename);
            Resource recurso = new UrlResource(ruta.toUri());
            if (!recurso.exists() || !recurso.isReadable())
                throw new ResourceNotFoundException("No se encontró la imagen: " + filename);
            return recurso;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("No se pudo leer la imagen: " + filename);
        }
    }

    @Override
    public String obtenerContentType(String filename) {
        String ext = StringUtils.getFilenameExtension(filename);
        if (ext == null) return "application/octet-stream";
        return switch (ext.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png"         -> "image/png";
            case "webp"        -> "image/webp";
            default            -> "application/octet-stream";
        };
    }
}
