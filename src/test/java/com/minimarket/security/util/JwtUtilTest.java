package com.minimarket.security.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        
        // Usamos ReflectionTestUtils para inyectar los @Value manualmente sin levantar Spring Boot
        ReflectionTestUtils.setField(jwtUtil, "secret", "EstaEsUnaClaveSuperSeguraParaTestearJWT2024Minimarket");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); // 1 hora de expiración

        // Creamos un UserDetails simulado (como el que devolvería Spring Security)
        userDetails = new User("testuser", "password123", 
                List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")));
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        // Act
        String token = jwtUtil.generateToken(userDetails);
        
        // Assert
        assertNotNull(token, "El token no debe ser nulo");
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals("testuser", extractedUsername, "El username extraído debe coincidir con el del token");
    }

    @Test
    void testExtractExpiration() {
        // Act
        String token = jwtUtil.generateToken(userDetails);
        Date expiration = jwtUtil.extractExpiration(token);
        
        // Assert
        assertNotNull(expiration, "La fecha de expiración no debe ser nula");
        assertTrue(expiration.after(new Date()), "La fecha de expiración debe estar en el futuro");
    }

    @Test
    void testIsTokenValidParaUsuarioCorrecto() {
        // Act
        String token = jwtUtil.generateToken(userDetails);
        boolean isValid = jwtUtil.isTokenValid(token, userDetails);
        
        // Assert
        assertTrue(isValid, "El token debería ser válido para el usuario que lo generó");
    }

    @Test
    void testIsTokenInvalidParaUsuarioDistinto() {
        // Arrange
        String token = jwtUtil.generateToken(userDetails);
        UserDetails otroUsuario = new User("otrousuario", "pass", 
                List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")));
        
        // Act
        boolean isValid = jwtUtil.isTokenValid(token, otroUsuario);
        
        // Assert
        assertFalse(isValid, "El token no debería ser válido si se evalúa contra un usuario distinto");
    }
}