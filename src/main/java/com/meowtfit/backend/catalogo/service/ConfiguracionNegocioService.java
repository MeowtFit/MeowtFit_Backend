package com.meowtfit.backend.catalogo.service;

import com.meowtfit.backend.catalogo.dto.ConfiguracionNegocioDTO;

public interface ConfiguracionNegocioService {
    ConfiguracionNegocioDTO obtenerConfiguracion();
    ConfiguracionNegocioDTO guardarOModificar(ConfiguracionNegocioDTO dto);
}