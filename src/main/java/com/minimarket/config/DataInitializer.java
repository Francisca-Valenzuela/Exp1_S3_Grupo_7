package com.minimarket.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(RolRepository rolRepository,
                                      UsuarioRepository usuarioRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Crear roles si no existen
            Rol rolGerente = rolRepository.findByNombre("ROLE_GERENTE")
                    .orElseGet(() -> rolRepository.save(crearRol("ROLE_GERENTE")));
            Rol rolEmpleado = rolRepository.findByNombre("ROLE_EMPLEADO")
                    .orElseGet(() -> rolRepository.save(crearRol("ROLE_EMPLEADO")));
            Rol rolCliente = rolRepository.findByNombre("ROLE_CLIENTE")
                    .orElseGet(() -> rolRepository.save(crearRol("ROLE_CLIENTE")));

            // Crear usuario gerente de prueba
            if (usuarioRepository.findByUsername("gerente").isEmpty()) {
                Usuario gerente = new Usuario();
                gerente.setUsername("gerente");
                gerente.setPassword(passwordEncoder.encode("gerente123"));
                Set<Rol> roles = new HashSet<>();
                roles.add(rolGerente);
                gerente.setRoles(roles);
                usuarioRepository.save(gerente);
            }

            // Crear usuario empleado de prueba
            if (usuarioRepository.findByUsername("empleado").isEmpty()) {
                Usuario empleado = new Usuario();
                empleado.setUsername("empleado");
                empleado.setPassword(passwordEncoder.encode("empleado123"));
                Set<Rol> roles = new HashSet<>();
                roles.add(rolEmpleado);
                empleado.setRoles(roles);
                usuarioRepository.save(empleado);
            }

            // Crear usuario cliente de prueba
            if (usuarioRepository.findByUsername("cliente").isEmpty()) {
                Usuario cliente = new Usuario();
                cliente.setUsername("cliente");
                cliente.setPassword(passwordEncoder.encode("cliente123"));
                Set<Rol> roles = new HashSet<>();
                roles.add(rolCliente);
                cliente.setRoles(roles);
                usuarioRepository.save(cliente);
            }

            System.out.println("Datos de prueba cargados: gerente/empleado/cliente");
        };
    }

    private Rol crearRol(String nombre) {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        return rol;
    }
}