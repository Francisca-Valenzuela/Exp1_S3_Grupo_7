package com.minimarket.controller;

import com.minimarket.dto.UsuarioRequestDTO;
import com.minimarket.dto.UsuarioResponseDTO;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.service.RolService;
import com.minimarket.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolService rolService;

    @GetMapping
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioService.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.findById(id)
                .map(usuario -> ResponseEntity.ok(mapToResponseDTO(usuario)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> guardarUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        if (usuarioService.findByUsername(dto.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        // Mapeo de los nuevos campos obligatorios
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setDireccion(dto.getDireccion());
        
        Rol rolCliente = rolService.findByNombre("ROLE_CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado en el sistema"));
        
        Set<Rol> roles = new HashSet<>();
        roles.add(rolCliente);
        usuario.setRoles(roles);

        Usuario guardado = usuarioService.save(usuario);
        return ResponseEntity.ok(mapToResponseDTO(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        Optional<Usuario> usuarioExistente = usuarioService.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setUsername(dto.getUsername());
            
            // Actualización de los nuevos campos obligatorios
            usuario.setNombre(dto.getNombre());
            usuario.setApellido(dto.getApellido());
            usuario.setEmail(dto.getEmail());
            usuario.setDireccion(dto.getDireccion());
            
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            
            Usuario actualizado = usuarioService.save(usuario);
            return ResponseEntity.ok(mapToResponseDTO(actualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private UsuarioResponseDTO mapToResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        if (usuario.getRoles() != null) {
            dto.setRoles(usuario.getRoles().stream()
                    .map(Rol::getNombre)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }
}