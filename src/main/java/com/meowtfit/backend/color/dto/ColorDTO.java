package com.meowtfit.backend.color.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColorDTO {
    private Long idColor;
    private String nombre;
    private String hexadecimal;
}
