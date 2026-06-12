package com.minimarket.security.controller;

import com.minimarket.dto.UsuarioRequestDTO;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;

import com.minimarket.security.model.LoginRequest;
import com.minimarket.security.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLoginExitoso() {
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        UserDetails userDetails = User.builder().username("user").password("pass").roles("CLIENTE").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtil.generateToken(userDetails)).thenReturn("fake-jwt-token");

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("fake-jwt-token", body.get("token"));
        assertEquals("user", body.get("username"));
    }

    @Test
    void testLoginFallido() {
        LoginRequest loginRequest = new LoginRequest("user", "wrong-pass");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Credenciales inválidas", body.get("error"));
    }

    @Test
    void testRegistroUsuarioExistente() {
        UsuarioRequestDTO request = new UsuarioRequestDTO();
        request.setUsername("existente");

        when(usuarioRepository.findByUsername("existente")).thenReturn(Optional.of(new Usuario()));

        ResponseEntity<?> response = authController.registro(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("El usuario ya existe", body.get("error"));
    }

    @Test
    void testRegistroExitoso() {
        UsuarioRequestDTO request = new UsuarioRequestDTO();
        request.setUsername("nuevo");
        request.setPassword("pass");
        request.setNombre("Test");
        request.setApellido("User");
        request.setEmail("test@test.com");
        request.setDireccion("Dir 123");

        Rol rolCliente = new Rol();
        rolCliente.setNombre("ROLE_CLIENTE");

        when(usuarioRepository.findByUsername("nuevo")).thenReturn(Optional.empty());
        when(rolRepository.findByNombre("ROLE_CLIENTE")).thenReturn(Optional.of(rolCliente));
        when(passwordEncoder.encode("pass")).thenReturn("encoded-pass");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(new Usuario());

        ResponseEntity<?> response = authController.registro(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Usuario registrado exitosamente", body.get("mensaje"));
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}