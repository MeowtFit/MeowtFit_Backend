package com.meowtfit.backend.color.mapper;

import com.meowtfit.backend.color.dto.ColorDTO;
import com.meowtfit.backend.color.entity.Color;
import org.springframework.stereotype.Component;

@Component
public class ColorMapper {

    public ColorDTO toDTO(Color color) {
        if (color == null) return null;
        return new ColorDTO(color.getIdColor(), color.getNombre(), color.getHexadecimal());
    }

    public Color toEntity(ColorDTO dto) {
        if (dto == null) return null;
        Color color = new Color();
        color.setIdColor(dto.getIdColor());
        color.setNombre(dto.getNombre());
        color.setHexadecimal(dto.getHexadecimal());
        return color;
    }
}
