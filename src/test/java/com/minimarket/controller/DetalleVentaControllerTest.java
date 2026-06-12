package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.service.DetalleVentaService;
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
class DetalleVentaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock private DetalleVentaService detalleVentaService;

    @InjectMocks
    private DetalleVentaController detalleVentaController;

    private DetalleVenta detalle;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(detalleVentaController).build();
        objectMapper = new ObjectMapper();

        detalle = new DetalleVenta();
        detalle.setId(1L);
        detalle.setCantidad(3);
        detalle.setPrecio(1500.0);
    }

    @Test
    void testListarDetalleVentas() throws Exception {
        when(detalleVentaService.findAll()).thenReturn(List.of(detalle));
        mockMvc.perform(get("/api/detalle-ventas"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerDetalleVentaPorId_Existente() throws Exception {
        when(detalleVentaService.findById(1L)).thenReturn(detalle);
        mockMvc.perform(get("/api/detalle-ventas/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerDetalleVentaPorId_Inexistente() throws Exception {
        when(detalleVentaService.findById(99L)).thenReturn(null);
        mockMvc.perform(get("/api/detalle-ventas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGuardarDetalleVenta() throws Exception {
        when(detalleVentaService.save(any(DetalleVenta.class))).thenReturn(detalle);
        mockMvc.perform(post("/api/detalle-ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detalle)))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarDetalleVenta_Existente() throws Exception {
        when(detalleVentaService.findById(1L)).thenReturn(detalle);
        when(detalleVentaService.save(any(DetalleVenta.class))).thenReturn(detalle);

        mockMvc.perform(put("/api/detalle-ventas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detalle)))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarDetalleVenta_Inexistente() throws Exception {
        when(detalleVentaService.findById(1L)).thenReturn(null);

        mockMvc.perform(put("/api/detalle-ventas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detalle)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarDetalleVenta_Existente() throws Exception {
        when(detalleVentaService.findById(1L)).thenReturn(detalle);
        doNothing().when(detalleVentaService).deleteById(1L);

        mockMvc.perform(delete("/api/detalle-ventas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarDetalleVenta_Inexistente() throws Exception {
        when(detalleVentaService.findById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/detalle-ventas/1"))
                .andExpect(status().isNotFound());
    }
}