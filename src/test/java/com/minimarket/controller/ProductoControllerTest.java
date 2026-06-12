package com.minimarket.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.ProductoController;
import com.minimarket.dto.ProductoDTO;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.service.CategoriaService;
import com.minimarket.service.ProductoService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock private ProductoService productoService;
    @Mock private CategoriaService categoriaService;

    @InjectMocks
    private ProductoController productoController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testListarProductos() throws Exception {
        when(productoService.findAll()).thenReturn(List.of(new Producto()));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    void testGuardarProducto_CategoriaNoExiste() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setCategoriaId(99L);
        dto.setNombre("Arroz");
        dto.setPrecio(1000.0);
        dto.setStock(10);

        when(categoriaService.findById(99L)).thenReturn(null);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGuardarProducto_Exitoso() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setCategoriaId(1L);
        dto.setNombre("Arroz");
        dto.setPrecio(1000.0);
        dto.setStock(10);

        when(categoriaService.findById(1L)).thenReturn(new Categoria());
        when(productoService.save(any(Producto.class))).thenReturn(new Producto());

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}