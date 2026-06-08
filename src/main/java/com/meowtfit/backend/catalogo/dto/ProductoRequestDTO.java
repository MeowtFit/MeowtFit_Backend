package com.meowtfit.backend.catalogo.dto;

import java.math.BigDecimal;
import com.meowtfit.backend.catalogo.entity.EstadoProducto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {
    private String nombre;
    private BigDecimal precioBase;
    private EstadoProducto estado;
    private String descripcion;
    private String imagenUrl;
    private Long idCategoria;
}
