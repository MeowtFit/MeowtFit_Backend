package com.meowtfit.backend.carrito.service;

import com.meowtfit.backend.carrito.dto.CarritoDTO;
import com.meowtfit.backend.carrito.dto.CarritoRequestDTO;

import java.util.List;

public interface CarritoService {

    CarritoDTO crearCarrito(CarritoRequestDTO dto);

    CarritoDTO crearCarritoParaUsuario(String correo);

    CarritoDTO obtenerCarritoActivoPorUsuario(Long idUsuario);

    CarritoDTO obtenerCarritoActivo(String correo);

    List<CarritoDTO> listarCarritosPorUsuario(Long idUsuario);

    List<CarritoDTO> listarCarritos(String correo);

    CarritoDTO desactivarCarrito(Long idCarrito);
}
