package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.dto.UsuarioRequestDTO;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.service.RolService;
import com.minimarket.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock private UsuarioService usuarioService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RolService rolService;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario usuario;
    private UsuarioRequestDTO dto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        objectMapper = new ObjectMapper();

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("test.user");
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setEmail("test@minimarket.cl");
        usuario.setDireccion("Santiago");

        dto = new UsuarioRequestDTO();
        dto.setUsername("test.user");
        dto.setPassword("password123");
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setEmail("test@minimarket.cl");
        dto.setDireccion("Santiago");
    }

    @Test
    void testListarUsuarios() throws Exception {
        when(usuarioService.findAll()).thenReturn(List.of(usuario));
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("test.user"));
    }

    @Test
    void testObtenerUsuarioPorId_Existente() throws Exception {
        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));
        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test.user"));
    }

    @Test
    void testObtenerUsuarioPorId_Inexistente() throws Exception {
        when(usuarioService.findById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGuardarUsuario_Exitoso() throws Exception {
        when(usuarioService.findByUsername("test.user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");
        when(rolService.findByNombre("ROLE_CLIENTE")).thenReturn(Optional.of(new Rol()));
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testGuardarUsuario_YaExiste() throws Exception {
        when(usuarioService.findByUsername("test.user")).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El usuario ya existe"));
    }

    @Test
    void testActualizarUsuario_Existente() throws Exception {
        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarUsuario_Inexistente() throws Exception {
        when(usuarioService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarUsuario_Existente() throws Exception {
        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioService).deleteById(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarUsuario_Inexistente() throws Exception {
        when(usuarioService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNotFound());
    }
}