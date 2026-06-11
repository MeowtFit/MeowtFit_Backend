package com.meowtfit.backend.negocio.service;

import com.meowtfit.backend.negocio.dto.ConfiguracionNegocioDTO;

public interface ConfiguracionNegocioService {
    ConfiguracionNegocioDTO obtenerConfiguracion();
    ConfiguracionNegocioDTO guardarOModificar(ConfiguracionNegocioDTO dto);
}
