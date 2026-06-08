package com.meowtfit.backend.lineaCarrito.service;

import com.meowtfit.backend.lineaCarrito.dto.LineaCarritoDTO;
import com.meowtfit.backend.lineaCarrito.dto.LineaCarritoRequestDTO;

import java.util.List;

public interface LineaCarritoService {

    LineaCarritoDTO crearLineaCarrito(LineaCarritoRequestDTO dto);

    LineaCarritoDTO actualizarLineaCarrito(Long idLineaCarrito, LineaCarritoRequestDTO dto);

    void eliminarLineaCarrito(Long idLineaCarrito);

    List<LineaCarritoDTO> listarLineasPorCarrito(Long idCarrito);

    List<LineaCarritoDTO> listarLineasPorVariante(Long idVariante);
}
