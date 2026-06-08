package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.ConfiguracionNegocioDTO;
import com.meowtfit.backend.catalogo.entity.ConfiguracionNegocio;
import com.meowtfit.backend.catalogo.mapper.ConfiguracionNegocioMapper;
import com.meowtfit.backend.catalogo.repository.ConfiguracionNegocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfiguracionNegocioServiceImpl implements ConfiguracionNegocioService {

    private final ConfiguracionNegocioRepository repo;
    private final ConfiguracionNegocioMapper mapper;

    @Override
    public ConfiguracionNegocioDTO obtenerConfiguracion() {
        return repo.findTopByOrderByIdConfiguracionAsc()
                .map(mapper::toDTO)
                .orElseGet(() -> {
                    // crear por defecto en memoria (no persistir automáticamente aquí)
                    ConfiguracionNegocioDTO d = new ConfiguracionNegocioDTO();
                    d.setStockMinimoCotizacion(1000);
                    d.setPorcentajePrecioPiso(java.math.BigDecimal.ZERO);
                    return d;
                });
    }

    @Override
    @Transactional
    public ConfiguracionNegocioDTO guardarOModificar(ConfiguracionNegocioDTO dto) {
        ConfiguracionNegocio entidad = repo.findTopByOrderByIdConfiguracionAsc().orElse(null);
        if (entidad == null) {
            entidad = mapper.toEntity(dto);
            // si no vino stock, setear 1000
            if (entidad.getStockMinimoCotizacion() == null) entidad.setStockMinimoCotizacion(1000);
        } else {
            // actualizar campos
            if (dto.getStockMinimoCotizacion() != null) entidad.setStockMinimoCotizacion(dto.getStockMinimoCotizacion());
            if (dto.getPorcentajePrecioPiso() != null) entidad.setPorcentajePrecioPiso(dto.getPorcentajePrecioPiso());
        }
        return mapper.toDTO(repo.save(entidad));
    }
}