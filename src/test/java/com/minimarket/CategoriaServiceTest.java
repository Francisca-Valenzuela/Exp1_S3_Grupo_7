package com.minimarket;

import com.minimarket.entity.Categoria;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.service.impl.CategoriaServiceImpl;
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
class CategoriaServiceTest {

    @Mock 
    private CategoriaRepository categoriaRepository;
    
    @InjectMocks 
    private CategoriaServiceImpl categoriaService;

    @Test 
    void testFindAll() {
        when(categoriaRepository.findAll()).thenReturn(List.of(new Categoria()));
        assertFalse(categoriaService.findAll().isEmpty());
    }

    @Test 
    void testFindById() {
        Categoria c = new Categoria(); 
        c.setId(1L);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(c));
        assertNotNull(categoriaService.findById(1L));
    }

    @Test 
    void testSave() {
        Categoria c = new Categoria();
        when(categoriaRepository.save(c)).thenReturn(c);
        assertNotNull(categoriaService.save(c));
    }

    @Test 
    void testDeleteById() {
        doNothing().when(categoriaRepository).deleteById(1L);
        categoriaService.deleteById(1L);
        verify(categoriaRepository, times(1)).deleteById(1L);
    }
}