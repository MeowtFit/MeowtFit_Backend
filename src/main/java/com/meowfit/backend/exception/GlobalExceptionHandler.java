package com.meowfit.backend.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Manejador global de excepciones para toda la aplicación
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Maneja recursos no encontrados (HTTP 404)
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> HandleResourceNotFound(ResourceNotFoundException ex) {
		Map<String, Object> respuesta = new HashMap<>();
		respuesta.put("status", 404);
		respuesta.put("error", "No encontrado");
		respuesta.put("mensaje", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
	}

	// Maneja solicitudes con datos inválidos (HTTP 400)
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Map<String, Object>> HandleBadRequest(BadRequestException ex) {
		Map<String, Object> respuesta = new HashMap<>();
		respuesta.put("status", 400);
		respuesta.put("error", "Solicitud inválida");
		respuesta.put("mensaje", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
	}

	// Maneja errores de validación de campos (@Valid) (HTTP 400)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> HandleValidationErrors(MethodArgumentNotValidException ex) {
		Map<String, Object> respuesta = new HashMap<>();
		Map<String, String> errores = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
			errores.put(error.getField(), error.getDefaultMessage())
		);
		respuesta.put("status", 400);
		respuesta.put("error", "Error de validación");
		respuesta.put("campos", errores);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
	}
	
	// Maneja credenciales inválidas o usuario inactivo en el login (HTTP 401)
	@ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
	public ResponseEntity<Map<String, Object>> HandleAuthenticationException(org.springframework.security.core.AuthenticationException ex) {
		Map<String, Object> respuesta = new HashMap<>();
		respuesta.put("status", 401);
		respuesta.put("error", "No autorizado");
		respuesta.put("mensaje", "Credenciales inválidas o usuario inactivo");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
	}

	// Maneja cualquier error inesperado (HTTP 500)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> HandleGenericException(Exception ex) {
		Map<String, Object> respuesta = new HashMap<>();
		respuesta.put("status", 500);
		respuesta.put("error", "Error interno del servidor");
		respuesta.put("mensaje", "Ocurrió un error inesperado");
		ex.printStackTrace(); // Imprime el error en consola
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
	}
    
}
