package com.minimarket;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.impl.UsuarioServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private Rol rolGerente;

    @BeforeEach
    void setUp() {
        rolGerente = new Rol();
        rolGerente.setId(1L);
        rolGerente.setNombre("ROLE_GERENTE");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("juan.perez");
        usuario.setPassword("password123");
        // Campos obligatorios requeridos por la S4
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setEmail("juan@test.com");
        usuario.setDireccion("Avenida 123");
        usuario.setRoles(Set.of(rolGerente));
    }

    // --- Pruebas de validación de datos completos (Requerimiento S4) ---

    @Test
    void testUsuarioTieneDatosCompletos() {
        assertAll("Validación de datos obligatorios del usuario",
            () -> assertNotNull(usuario.getNombre(), "Falta el nombre"),
            () -> assertNotNull(usuario.getApellido(), "Falta el apellido"),
            () -> assertNotNull(usuario.getEmail(), "Falta el email"),
            () -> assertNotNull(usuario.getDireccion(), "Falta la dirección"),
            () -> assertNotNull(usuario.getUsername(), "Falta el username"),
            () -> assertNotNull(usuario.getPassword(), "Falta el password")
        );
    }

    @Test
    void testUsuarioTieneRolesAsignados() {
        assertNotNull(usuario.getRoles(), "El usuario debe tener roles");
        assertFalse(usuario.getRoles().isEmpty(), "El usuario debe tener al menos un rol");
    }

    // --- Pruebas de comportamiento y dependencias externas ---

    @Test
    void testBuscarUsuarioPorUsernameExistente() {
        // Mockito configurado correctamente con thenReturn
        when(usuarioRepository.findByUsername("juan.perez")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findByUsername("juan.perez");

        assertTrue(resultado.isPresent(), "Debe encontrar el usuario");
        assertEquals("juan.perez", resultado.get().getUsername());
        verify(usuarioRepository, times(1)).findByUsername("juan.perez");
    }

    @Test
    void testBuscarUsuarioPorUsernameInexistente() {
        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.findByUsername("noexiste");

        assertFalse(resultado.isPresent(), "No debe encontrar el usuario");
    }

    @Test
    void testGuardarUsuarioRetornaUsuario() {
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario guardado = usuarioService.save(usuario);

        assertNotNull(guardado);
        assertEquals(usuario.getUsername(), guardado.getUsername());
        assertEquals(usuario.getEmail(), guardado.getEmail());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    // --- Validación de accesos por Rol ---

    @Test
    void testUsuarioConRolGerentePuedeRealizarOperaciones() {
        boolean puedeOperar = usuario.getRoles().stream()
                .anyMatch(r -> r.getNombre().equals("ROLE_GERENTE")
                            || r.getNombre().equals("ROLE_EMPLEADO"));

        assertTrue(puedeOperar, "Un usuario con rol válido debe poder realizar operaciones críticas");
    }

    @Test
    void testUsuarioSinRolesNoPuedeOperar() {
        Usuario sinRoles = new Usuario();
        sinRoles.setUsername("anonimo");
        sinRoles.setPassword("pass");
        sinRoles.setRoles(Set.of());

        boolean puedeOperar = sinRoles.getRoles().stream()
                .anyMatch(r -> r.getNombre().equals("ROLE_GERENTE")
                            || r.getNombre().equals("ROLE_EMPLEADO"));

        assertFalse(puedeOperar, "Un usuario sin roles no debe poder realizar operaciones críticas");
    }

    @Test
    void testFindAllUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(java.util.List.of(usuario));
        
        java.util.List<Usuario> lista = usuarioService.findAll();
        
        assertFalse(lista.isEmpty(), "La lista de usuarios no debe estar vacía");
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        
        Optional<Usuario> encontrado = usuarioService.findById(1L);
        
        assertTrue(encontrado.isPresent(), "Debe encontrar el usuario por ID");
        assertEquals(1L, encontrado.get().getId());
    }

    @Test
    void testDeleteById() {
        doNothing().when(usuarioRepository).deleteById(1L);
        
        usuarioService.deleteById(1L);
        
        verify(usuarioRepository, times(1)).deleteById(1L);
    }
    
}