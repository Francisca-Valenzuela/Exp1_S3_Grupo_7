package com.minimarket;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.repository.DetalleVentaRepository;
import com.minimarket.service.impl.DetalleVentaServiceImpl;
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
class DetalleVentaServiceTest {

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @InjectMocks
    private DetalleVentaServiceImpl detalleVentaService;

    @Test
    void testFindAll() {
        when(detalleVentaRepository.findAll()).thenReturn(List.of(new DetalleVenta()));
        assertFalse(detalleVentaService.findAll().isEmpty());
    }

    @Test
    void testFindById() {
        DetalleVenta d = new DetalleVenta();
        d.setId(1L);
        when(detalleVentaRepository.findById(1L)).thenReturn(Optional.of(d));
        assertNotNull(detalleVentaService.findById(1L));
    }

    @Test
    void testSave() {
        DetalleVenta d = new DetalleVenta();
        when(detalleVentaRepository.save(d)).thenReturn(d);
        assertNotNull(detalleVentaService.save(d));
    }

    @Test
    void testDeleteById() {
        doNothing().when(detalleVentaRepository).deleteById(1L);
        detalleVentaService.deleteById(1L);
        verify(detalleVentaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByVentaId() {
        when(detalleVentaRepository.findByVentaId(1L)).thenReturn(List.of(new DetalleVenta()));
        assertFalse(detalleVentaService.findByVentaId(1L).isEmpty());
    }
}