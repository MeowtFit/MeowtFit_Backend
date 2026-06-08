package com.meowtfit.backend.carrito.service;

import com.meowtfit.backend.carrito.dto.CarritoDTO;
import com.meowtfit.backend.carrito.dto.CarritoRequestDTO;
import com.meowtfit.backend.carrito.entity.Carrito;
import com.meowtfit.backend.carrito.mapper.CarritoMapper;
import com.meowtfit.backend.carrito.repository.CarritoRepository;
import com.meowtfit.backend.common.Estado;
import com.meowtfit.backend.exception.BadRequestException;
import com.meowtfit.backend.exception.ResourceNotFoundException;
import com.meowtfit.backend.usuario.entity.Usuario;
import com.meowtfit.backend.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoMapper carritoMapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public CarritoDTO crearCarrito(CarritoRequestDTO dto) {
        if (dto == null || dto.getIdUsuario() == null) {
            throw new BadRequestException("El id de usuario es obligatorio para crear un carrito");
        }

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con id: " + dto.getIdUsuario()));

        carritoRepository.findByUsuarioIdUsuarioAndEstado(dto.getIdUsuario(), Estado.ACTIVO)
                .ifPresent(c -> {
                    throw new BadRequestException("El usuario ya tiene un carrito activo");
                });

        Carrito carrito = carritoMapper.toEntity(dto, usuario);
        if (carrito.getEstado() == null) {
            carrito.setEstado(Estado.ACTIVO);
        }

        Carrito guardado = carritoRepository.save(carrito);
        return carritoMapper.toDTO(guardado);
    }

    @Override
    @Transactional
    public CarritoDTO crearCarritoParaUsuario(String correo) {
        Usuario usuario = obtenerUsuarioPorCorreo(correo);

        carritoRepository.findByUsuarioIdUsuarioAndEstado(usuario.getIdUsuario(), Estado.ACTIVO)
                .ifPresent(c -> {
                    throw new BadRequestException("El usuario ya tiene un carrito activo");
                });

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setEstado(Estado.ACTIVO);

        Carrito guardado = carritoRepository.save(carrito);
        return carritoMapper.toDTO(guardado);
    }

    @Override
    public CarritoDTO obtenerCarritoActivoPorUsuario(Long idUsuario) {
        return carritoRepository.findByUsuarioIdUsuarioAndEstado(idUsuario, Estado.ACTIVO)
                .map(carritoMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un carrito activo para el usuario con id: " + idUsuario));
    }

    @Override
    public CarritoDTO obtenerCarritoActivo(String correo) {
        Usuario usuario = obtenerUsuarioPorCorreo(correo);
        return obtenerCarritoActivoPorUsuario(usuario.getIdUsuario());
    }

    @Override
    public List<CarritoDTO> listarCarritosPorUsuario(Long idUsuario) {
        return carritoRepository.findByUsuarioIdUsuario(idUsuario)
                .stream()
                .map(carritoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarritoDTO> listarCarritos(String correo) {
        Usuario usuario = obtenerUsuarioPorCorreo(correo);
        return listarCarritosPorUsuario(usuario.getIdUsuario());
    }

    private Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con correo: " + correo));
    }

    @Override
    @Transactional
    public CarritoDTO desactivarCarrito(Long idCarrito) {
        Carrito carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrito no encontrado con id: " + idCarrito));

        carrito.setEstado(Estado.INACTIVO);
        Carrito guardado = carritoRepository.save(carrito);
        return carritoMapper.toDTO(guardado);
    }
}
