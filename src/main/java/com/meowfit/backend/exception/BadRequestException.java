package com.meowfit.backend.exception;

// Excepción para solicitudes con datos inválidos o incompletos (HTTP 400)
public class BadRequestException extends RuntimeException{
    
    public BadRequestException(String mensaje) {
		super(mensaje);
	}
    
}
