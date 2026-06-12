package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VentaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private VentaService ventaService;

    @InjectMocks
    private VentaController ventaController;

    private Venta venta;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ventaController).build();
        objectMapper = new ObjectMapper();
        
        venta = new Venta();
        venta.setId(1L);
    }

    @Test
    void testListarVentas() throws Exception {
        when(ventaService.findAll()).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerVentaPorId_Existente() throws Exception {
        when(ventaService.findById(1L)).thenReturn(venta);

        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerVentaPorId_Inexistente() throws Exception {
        when(ventaService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGuardarVenta() throws Exception {
        when(ventaService.save(any(Venta.class))).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isOk());
    }
}