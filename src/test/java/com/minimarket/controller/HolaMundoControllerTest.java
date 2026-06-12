package com.minimarket.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HolaMundoControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private HolaMundoController holaMundoController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(holaMundoController).build();
    }

    @Test
    void testHolaMundo() throws Exception {
        mockMvc.perform(get("/public/hola"))
                .andExpect(status().isOk())
                .andExpect(content().string("¡Hola Mundo!"));
    }
}