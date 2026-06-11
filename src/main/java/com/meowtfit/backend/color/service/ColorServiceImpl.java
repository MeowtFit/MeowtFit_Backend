package com.meowtfit.backend.color.service;

import com.meowtfit.backend.color.dto.ColorDTO;
import com.meowtfit.backend.color.entity.Color;
import com.meowtfit.backend.color.mapper.ColorMapper;
import com.meowtfit.backend.color.repository.ColorRepository;
import com.meowtfit.backend.exception.BadRequestException;
import com.meowtfit.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;
    private final ColorMapper colorMapper;

    @Override
    public List<ColorDTO> listarTodos() {
        return colorRepository.findAll().stream()
                .map(colorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ColorDTO registrarColor(ColorDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del color no puede estar vacío");
        }
        if (dto.getHexadecimal() == null || dto.getHexadecimal().isBlank()) {
            throw new BadRequestException("El hexadecimal del color no puede estar vacío");
        }

        Color color = colorMapper.toEntity(dto);
        Color guardado = colorRepository.save(color);
        return colorMapper.toDTO(guardado);
    }

    @Override
    public ColorDTO editarColor(Long idColor, ColorDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del color no puede estar vacío");
        }
        if (dto.getHexadecimal() == null || dto.getHexadecimal().isBlank()) {
            throw new BadRequestException("El hexadecimal del color no puede estar vacío");
        }

        Color color = colorRepository.findById(idColor)
                .orElseThrow(() -> new ResourceNotFoundException("Color no encontrado con id: " + idColor));

        color.setNombre(dto.getNombre());
        color.setHexadecimal(dto.getHexadecimal());

        Color guardado = colorRepository.save(color);
        return colorMapper.toDTO(guardado);
    }

    @Override
    public void eliminarColor(Long idColor) {
        Color color = colorRepository.findById(idColor)
                .orElseThrow(() -> new ResourceNotFoundException("Color no encontrado con id: " + idColor));
        colorRepository.delete(color);
    }
}
