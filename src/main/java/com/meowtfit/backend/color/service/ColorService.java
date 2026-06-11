package com.meowtfit.backend.color.service;

import com.meowtfit.backend.color.dto.ColorDTO;

import java.util.List;

public interface ColorService {
    List<ColorDTO> listarTodos();
    ColorDTO registrarColor(ColorDTO dto);
    ColorDTO editarColor(Long idColor, ColorDTO dto);
    void eliminarColor(Long idColor);
}
