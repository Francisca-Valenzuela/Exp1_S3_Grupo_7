package com.minimarket.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private Set<String> roles;
}