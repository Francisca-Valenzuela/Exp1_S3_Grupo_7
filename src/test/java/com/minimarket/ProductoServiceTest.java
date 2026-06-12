package com.minimarket;

import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.impl.ProductoServiceImpl;
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
class ProductoServiceTest {

    @Mock 
    private ProductoRepository productoRepository;
    
    @InjectMocks 
    private ProductoServiceImpl productoService;

    @Test 
    void testFindAll() {
        when(productoRepository.findAll()).thenReturn(List.of(new Producto()));
        assertFalse(productoService.findAll().isEmpty());
    }

    @Test 
    void testFindById() {
        Producto p = new Producto(); 
        p.setId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));
        assertNotNull(productoService.findById(1L));
    }

    @Test 
    void testSave() {
        Producto p = new Producto();
        when(productoRepository.save(p)).thenReturn(p);
        assertNotNull(productoService.save(p));
    }

    @Test 
    void testDeleteById() {
        doNothing().when(productoRepository).deleteById(1L);
        productoService.deleteById(1L);
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test 
    void testFindByCategoriaId() {
        when(productoRepository.findByCategoriaId(1L)).thenReturn(List.of(new Producto()));
        assertFalse(productoService.findByCategoriaId(1L).isEmpty());
    }
}