package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CarritoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock private CarritoService carritoService;

    @InjectMocks
    private CarritoController carritoController;

    private Carrito carrito;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(carritoController).build();
        objectMapper = new ObjectMapper();

        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setCantidad(2);
    }

    @Test
    void testListarCarrito() throws Exception {
        when(carritoService.findAll()).thenReturn(List.of(carrito));
        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerCarritoPorId_Existente() throws Exception {
        when(carritoService.findById(1L)).thenReturn(carrito);
        mockMvc.perform(get("/api/carrito/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerCarritoPorId_Inexistente() throws Exception {
        when(carritoService.findById(99L)).thenReturn(null);
        mockMvc.perform(get("/api/carrito/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAgregarProductoAlCarrito() throws Exception {
        when(carritoService.save(any(Carrito.class))).thenReturn(carrito);
        mockMvc.perform(post("/api/carrito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carrito)))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarCarrito_Existente() throws Exception {
        when(carritoService.findById(1L)).thenReturn(carrito);
        when(carritoService.save(any(Carrito.class))).thenReturn(carrito);

        mockMvc.perform(put("/api/carrito/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carrito)))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarCarrito_Inexistente() throws Exception {
        when(carritoService.findById(1L)).thenReturn(null);

        mockMvc.perform(put("/api/carrito/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carrito)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarProductoDelCarrito_Existente() throws Exception {
        when(carritoService.findById(1L)).thenReturn(carrito);
        doNothing().when(carritoService).deleteById(1L);

        mockMvc.perform(delete("/api/carrito/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarProductoDelCarrito_Inexistente() throws Exception {
        when(carritoService.findById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/carrito/1"))
                .andExpect(status().isNotFound());
    }
}