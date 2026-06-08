package com.meowtfit.backend.catalogo.mapper;

import com.meowtfit.backend.catalogo.dto.ConfiguracionNegocioDTO;
import com.meowtfit.backend.catalogo.entity.ConfiguracionNegocio;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracionNegocioMapper {

    public ConfiguracionNegocioDTO toDTO(ConfiguracionNegocio e) {
        if (e == null) return null;
        ConfiguracionNegocioDTO dto = new ConfiguracionNegocioDTO();
        dto.setIdConfiguracion(e.getIdConfiguracion());
        dto.setStockMinimoCotizacion(e.getStockMinimoCotizacion());
        dto.setPorcentajePrecioPiso(e.getPorcentajePrecioPiso());
        return dto;
    }

    public ConfiguracionNegocio toEntity(ConfiguracionNegocioDTO dto) {
        if (dto == null) return null;
        ConfiguracionNegocio e = new ConfiguracionNegocio();
        e.setIdConfiguracion(dto.getIdConfiguracion());
        e.setStockMinimoCotizacion(dto.getStockMinimoCotizacion() != null ? dto.getStockMinimoCotizacion() : 1000);
        e.setPorcentajePrecioPiso(dto.getPorcentajePrecioPiso() != null ? dto.getPorcentajePrecioPiso() : e.getPorcentajePrecioPiso());
        return e;
    }
}