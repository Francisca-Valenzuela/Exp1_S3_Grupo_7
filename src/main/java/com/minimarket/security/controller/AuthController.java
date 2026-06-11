package com.minimarket.security.controller;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.security.model.LoginRequest;
import com.minimarket.security.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Endpoint de autenticación (login).
     * Valida credenciales y devuelve un JWT si son correctas.
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Solo logueamos el username, NUNCA la contraseña ni el token completo
        log.info("Intento de inicio de sesión - usuario: {}", loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            // Roles como lista para consistencia con el claim del JWT
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .collect(Collectors.toList());

            // Log de éxito: solo username, nunca el token completo
            log.info("Login exitoso - usuario: {}", loginRequest.getUsername());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "tipo", "Bearer",
                    "username", userDetails.getUsername(),
                    "roles", roles
            ));

        } catch (Exception e) {
            // No revelamos la razón exacta del fallo al cliente por seguridad
            log.warn("Fallo de autenticación - usuario: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    /**
     * Endpoint de registro de nuevos usuarios.
     * Asigna el rol CLIENTE por defecto.
     * POST /api/auth/registro
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody LoginRequest request) {
        log.info("Iniciando proceso de registro - usuario: {}", request.getUsername());

        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Fallo de registro: usuario ya existe - {}", request.getUsername());
            return ResponseEntity.badRequest().body(Map.of("error", "El usuario ya existe"));
        }

        Rol rolCliente = rolRepository.findByNombre("ROLE_CLIENTE")
                .orElseGet(() -> {
                    log.info("Creando rol ROLE_CLIENTE en la base de datos.");
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombre("ROLE_CLIENTE");
                    return rolRepository.save(nuevoRol);
                });

        Set<Rol> roles = new HashSet<>();
        roles.add(rolCliente);

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(request.getUsername());
        // Contraseña encriptada con BCrypt, nunca en texto plano
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setRoles(roles);
        usuarioRepository.save(nuevoUsuario);

        log.info("Usuario registrado exitosamente - usuario: {}", request.getUsername());
        return ResponseEntity.ok(Map.of(
                "mensaje", "Usuario registrado exitosamente",
                "username", request.getUsername(),
                "rol", "ROLE_CLIENTE"
        ));
    }
}