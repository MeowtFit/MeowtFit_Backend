package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.ReglaDescuentoDTO;
import java.util.List;

public interface ReglaDescuentoService {
    ReglaDescuentoDTO crearRegla(ReglaDescuentoDTO dto);
    ReglaDescuentoDTO actualizarRegla(Long id, ReglaDescuentoDTO dto);
    List<ReglaDescuentoDTO> listarPorProducto(Long idProducto);
    void eliminarRegla(Long id);
}