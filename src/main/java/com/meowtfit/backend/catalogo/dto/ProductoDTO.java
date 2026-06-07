package com.meowtfit.backend.catalogo.dto;

import com.meowtfit.backend.catalogo.entity.EstadoProducto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long idProducto;
    private String nombre;
    private BigDecimal precioBase;
    private EstadoProducto estado;
    private String descripcion;
    private String imagenUrl;
    
    private CategoriaDTO categoria;
    private List<VarianteProductoDTO> variantes;
}
