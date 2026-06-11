package com.meowtfit.backend.color.controller;

import com.meowtfit.backend.color.dto.ColorDTO;
import com.meowtfit.backend.color.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colores")
@RequiredArgsConstructor
public class ColorController {

    private final ColorService colorService;

    @GetMapping
    public ResponseEntity<List<ColorDTO>> listarTodos() {
        return ResponseEntity.ok(colorService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<ColorDTO> registrarColor(@RequestBody ColorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(colorService.registrarColor(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColorDTO> editarColor(@PathVariable Long id, @RequestBody ColorDTO dto) {
        return ResponseEntity.ok(colorService.editarColor(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarColor(@PathVariable Long id) {
        colorService.eliminarColor(id);
        return ResponseEntity.noContent().build();
    }
}
