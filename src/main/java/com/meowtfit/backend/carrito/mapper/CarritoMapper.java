package com.meowtfit.backend.carrito.mapper;

import org.springframework.stereotype.Component;

import com.meowtfit.backend.carrito.dto.CarritoDTO;
import com.meowtfit.backend.carrito.dto.CarritoRequestDTO;
import com.meowtfit.backend.carrito.entity.Carrito;
import com.meowtfit.backend.usuario.entity.Usuario;

@Component
public class CarritoMapper {

    // Método para convertir de entidad a DTO
    public CarritoDTO toDTO(Carrito carrito) {
        if (carrito == null) return null;

        CarritoDTO dto = new CarritoDTO();
        dto.setIdCarrito(carrito.getIdCarrito());
        dto.setFechaCreacion(carrito.getFechaCreacion());
        dto.setEstado(carrito.getEstado());
        dto.setIdUsuario(carrito.getUsuario() != null ? carrito.getUsuario().getIdUsuario() : null);
        return dto;
    }

    // Método para convertir de DTO a entidad
    public Carrito toEntity(CarritoRequestDTO dto, Usuario usuario) {
        if (dto == null) return null;

        Carrito carrito = new Carrito();
        carrito.setEstado(null); // se establece por defecto en la entidad
        carrito.setUsuario(usuario);
        return carrito;
    }
}
