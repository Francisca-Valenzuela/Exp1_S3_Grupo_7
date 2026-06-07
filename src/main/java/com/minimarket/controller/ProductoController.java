package com.minimarket.controller;

import com.minimarket.dto.ProductoDTO;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Categoria;
import com.minimarket.service.ProductoService;
import com.minimarket.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE')") 
    public List<Producto> listarProductos() {
        return productoService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE')") 
    public ResponseEntity<?> guardarProducto(@Valid @RequestBody ProductoDTO dto) {
        Categoria categoria = categoriaService.findById(dto.getCategoriaId());
        if (categoria == null) {
            return ResponseEntity.badRequest().body("La categoría especificada no existe");
        }
        
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(categoria);
        
        return ResponseEntity.ok(productoService.save(producto));
    }
}