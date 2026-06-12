package com.minimarket;

import com.minimarket.entity.Rol;
import com.minimarket.repository.RolRepository;
import com.minimarket.service.impl.RolServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolServiceImpl rolService;

    @Test
    void testFindByNombre() {
        // Arrange (Preparar)
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ROLE_CLIENTE");

        when(rolRepository.findByNombre("ROLE_CLIENTE")).thenReturn(Optional.of(rol));

        // Act (Ejecutar)
        Optional<Rol> encontrado = rolService.findByNombre("ROLE_CLIENTE");

        // Assert (Validar)
        assertTrue(encontrado.isPresent(), "Debe encontrar el rol");
        assertEquals("ROLE_CLIENTE", encontrado.get().getNombre(), "El nombre del rol debe coincidir");
        verify(rolRepository, times(1)).findByNombre("ROLE_CLIENTE");
    }
}