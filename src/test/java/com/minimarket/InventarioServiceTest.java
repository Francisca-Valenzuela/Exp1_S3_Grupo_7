package com.minimarket;

import com.minimarket.entity.Inventario;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;
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
class InventarioServiceTest {

    @Mock 
    private InventarioRepository inventarioRepository;
    
    @InjectMocks 
    private InventarioServiceImpl inventarioService;

    @Test 
    void testFindAll() {
        when(inventarioRepository.findAll()).thenReturn(List.of(new Inventario()));
        assertFalse(inventarioService.findAll().isEmpty());
    }

    @Test 
    void testFindById() {
        Inventario i = new Inventario(); 
        i.setId(1L);
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(i));
        assertNotNull(inventarioService.findById(1L));
    }

    @Test 
    void testSave() {
        Inventario i = new Inventario();
        when(inventarioRepository.save(i)).thenReturn(i);
        assertNotNull(inventarioService.save(i));
    }

    @Test 
    void testDeleteById() {
        doNothing().when(inventarioRepository).deleteById(1L);
        inventarioService.deleteById(1L);
        verify(inventarioRepository, times(1)).deleteById(1L);
    }

    @Test 
    void testFindByProductoId() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(new Inventario()));
        assertFalse(inventarioService.findByProductoId(1L).isEmpty());
    }
}