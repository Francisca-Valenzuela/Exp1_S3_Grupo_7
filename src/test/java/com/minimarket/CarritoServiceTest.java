package com.minimarket;

import com.minimarket.entity.Carrito;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.service.impl.CarritoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    @Test
    void testFindAll() {
        when(carritoRepository.findAll()).thenReturn(List.of(new Carrito()));
        assertFalse(carritoService.findAll().isEmpty());
    }

    @Test
    void testFindById() {
        Carrito c = new Carrito();
        c.setId(1L);
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(c));
        assertNotNull(carritoService.findById(1L));
    }

    @Test
    void testSave() {
        Carrito c = new Carrito();
        when(carritoRepository.save(c)).thenReturn(c);
        assertNotNull(carritoService.save(c));
    }

    @Test
    void testDeleteById() {
        doNothing().when(carritoRepository).deleteById(1L);
        carritoService.deleteById(1L);
        verify(carritoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByUsuarioId() {
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(List.of(new Carrito()));
        assertFalse(carritoService.findByUsuarioId(1L).isEmpty());
    }
}