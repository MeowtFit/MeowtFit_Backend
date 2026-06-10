package com.meowtfit.backend.lineaCarrito.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meowtfit.backend.carrito.entity.Carrito;
import com.meowtfit.backend.carrito.repository.CarritoRepository;
import com.meowtfit.backend.catalogo.entity.VarianteProducto;
import com.meowtfit.backend.catalogo.repository.VarianteProductoRepository;
import com.meowtfit.backend.exception.ResourceNotFoundException;
import com.meowtfit.backend.lineaCarrito.dto.LineaCarritoDTO;
import com.meowtfit.backend.lineaCarrito.dto.LineaCarritoRequestDTO;
import com.meowtfit.backend.lineaCarrito.entity.LineaCarrito;
import com.meowtfit.backend.lineaCarrito.mapper.LineaCarritoMapper;
import com.meowtfit.backend.lineaCarrito.repository.LineaCarritoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineaCarritoServiceImpl implements LineaCarritoService {

    private final LineaCarritoRepository lineaCarritoRepository;
    private final LineaCarritoMapper lineaCarritoMapper;
    private final CarritoRepository carritoRepository;
    private final VarianteProductoRepository varianteProductoRepository;

    @Override
    @Transactional
    public LineaCarritoDTO crearLineaCarrito(LineaCarritoRequestDTO dto) {
        Carrito carrito = carritoRepository.findById(dto.getIdCarrito())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrito no encontrado con id: " + dto.getIdCarrito()));

        VarianteProducto variante = varianteProductoRepository.findById(dto.getIdVariante())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Variante de producto no encontrada con id: " + dto.getIdVariante()));

        LineaCarrito linea = lineaCarritoMapper.toEntity(dto, carrito, variante);
        if (linea.getSubtotal() == null) {
            linea.setSubtotal(calculateSubtotal(linea));
        }

        return lineaCarritoMapper.toDTO(lineaCarritoRepository.save(linea));
    }

    @Override
    @Transactional
    public LineaCarritoDTO actualizarLineaCarrito(Long idLineaCarrito, LineaCarritoRequestDTO dto) {
        LineaCarrito linea = lineaCarritoRepository.findById(idLineaCarrito)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Linea de carrito no encontrada con id: " + idLineaCarrito));

        if (dto.getIdCarrito() != null && !dto.getIdCarrito().equals(linea.getCarrito().getIdCarrito())) {
            Carrito carrito = carritoRepository.findById(dto.getIdCarrito())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Carrito no encontrado con id: " + dto.getIdCarrito()));
            linea.setCarrito(carrito);
        }

        if (dto.getIdVariante() != null && !dto.getIdVariante().equals(linea.getVarianteProducto().getIdVariante())) {
            VarianteProducto variante = varianteProductoRepository.findById(dto.getIdVariante())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Variante de producto no encontrada con id: " + dto.getIdVariante()));
            linea.setVarianteProducto(variante);
        }

        if (dto.getCantidad() != null) {
            linea.setCantidad(dto.getCantidad());
        }
        if (dto.getPrecioUnitario() != null) {
            linea.setPrecioUnitario(dto.getPrecioUnitario());
        }
        if (dto.getSubtotal() != null) {
            linea.setSubtotal(dto.getSubtotal());
        } else {
            linea.setSubtotal(calculateSubtotal(linea));
        }

        return lineaCarritoMapper.toDTO(lineaCarritoRepository.save(linea));
    }

    @Override
    @Transactional
    public void eliminarLineaCarrito(Long idLineaCarrito) {
        LineaCarrito linea = lineaCarritoRepository.findById(idLineaCarrito)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Linea de carrito no encontrada con id: " + idLineaCarrito));
        lineaCarritoRepository.delete(linea);
    }

    @Override
    public List<LineaCarritoDTO> listarLineasPorCarrito(Long idCarrito) {
        return lineaCarritoRepository.findByCarritoIdCarrito(idCarrito).stream()
                .map(lineaCarritoMapper::toDTO)
                .toList();
    }

    @Override
    public List<LineaCarritoDTO> listarLineasPorVariante(Long idVariante) {
        return lineaCarritoRepository.findByVarianteProductoIdVariante(idVariante).stream()
                .map(lineaCarritoMapper::toDTO)
                .toList();
    }

    private BigDecimal calculateSubtotal(LineaCarrito linea) {
        return linea.getPrecioUnitario().multiply(BigDecimal.valueOf(linea.getCantidad()));
    }
}
