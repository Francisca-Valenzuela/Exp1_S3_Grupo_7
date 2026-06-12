package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.service.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InventarioControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock private InventarioService inventarioService;

    @InjectMocks
    private InventarioController inventarioController;

    private Inventario inventario;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventarioController).build();
        objectMapper = new ObjectMapper();

        Producto producto = new Producto();
        producto.setId(1L);

        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(50);
        inventario.setTipoMovimiento("Entrada");
        inventario.setFechaMovimiento(new Date());
    }

    @Test
    void testListarMovimientosDeInventario() throws Exception {
        when(inventarioService.findAll()).thenReturn(List.of(inventario));
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerMovimientoPorId_Existente() throws Exception {
        when(inventarioService.findById(1L)).thenReturn(inventario);
        mockMvc.perform(get("/api/inventario/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerMovimientoPorId_Inexistente() throws Exception {
        when(inventarioService.findById(99L)).thenReturn(null);
        mockMvc.perform(get("/api/inventario/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRegistrarMovimiento() throws Exception {
        when(inventarioService.save(any(Inventario.class))).thenReturn(inventario);
        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk());
    }

    @Test
    void testRegistrarMovimiento_ProductoNull() throws Exception {
        inventario.setProducto(null);
        when(inventarioService.save(any(Inventario.class))).thenReturn(inventario);
        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarMovimiento_Existente() throws Exception {
        when(inventarioService.findById(1L)).thenReturn(inventario);
        when(inventarioService.save(any(Inventario.class))).thenReturn(inventario);

        mockMvc.perform(put("/api/inventario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarMovimiento_Inexistente() throws Exception {
        when(inventarioService.findById(1L)).thenReturn(null);

        mockMvc.perform(put("/api/inventario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarMovimiento_Existente() throws Exception {
        when(inventarioService.findById(1L)).thenReturn(inventario);
        doNothing().when(inventarioService).deleteById(1L);

        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarMovimiento_Inexistente() throws Exception {
        when(inventarioService.findById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isNotFound());
    }
}