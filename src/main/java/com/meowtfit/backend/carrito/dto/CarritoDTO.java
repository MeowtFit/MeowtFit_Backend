package com.meowtfit.backend.carrito.dto;

import java.time.LocalDateTime;

import com.meowtfit.backend.common.Estado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {
    private Long idCarrito;
    private LocalDateTime fechaCreacion;
    private Estado estado;
    private Long idUsuario;
}
