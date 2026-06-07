package com.minimarket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Estructura base reutilizable para todas las respuestas de error
    private Map<String, Object> buildError(String mensaje, int codigo) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("codigo", codigo);
        error.put("mensaje", mensaje);
        return error;
    }

    // Errores de validación (@Valid / @NotBlank, @Size, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            campos.put(fieldName, errorMessage);
        });
        Map<String, Object> respuesta = buildError("Error de validación en los datos enviados", 400);
        respuesta.put("campos", campos);
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    // Acceso denegado por rol insuficiente (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> respuesta = buildError(
                "No tienes permisos para acceder a este recurso", 403);
        return new ResponseEntity<>(respuesta, HttpStatus.FORBIDDEN);
    }

    // Errores de negocio controlados (RuntimeException)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> respuesta = buildError(ex.getMessage(), 400);
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    // Cualquier otro error interno inesperado (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalExceptions(Exception ex) {
        Map<String, Object> respuesta = buildError(
                "Ha ocurrido un error interno. Contacte al administrador.", 500);
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}