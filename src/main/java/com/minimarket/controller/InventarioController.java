package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    // LECTURAS: Permitidas para Gerente y Empleado (necesitan ver stock)
    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    public List<Inventario> listarMovimientosDeInventario() {
        return inventarioService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    public ResponseEntity<Inventario> obtenerMovimientoPorId(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        return (inventario != null) ? ResponseEntity.ok(inventario) : ResponseEntity.notFound().build();
    }

    // ESCRITURAS: Restringidas estrictamente al Gerente
    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public Inventario registrarMovimiento(@RequestBody Inventario inventario) {
        // En una app real, el ID del producto no debería ser null aquí. Extraemos el ID con seguridad.
        Long productoId = (inventario.getProducto() != null) ? inventario.getProducto().getId() : null;
        log.info("Auditoría - Registrando nuevo movimiento de inventario: Producto ID [{}], Tipo [{}], Cantidad [{}]", 
                 productoId, inventario.getTipoMovimiento(), inventario.getCantidad());
                 
        return inventarioService.save(inventario);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Inventario> actualizarMovimiento(@PathVariable Long id, @RequestBody Inventario inventario) {
        log.info("Auditoría - Intento de actualización de movimiento de inventario ID: {}", id);
        
        Inventario existente = inventarioService.findById(id);
        if (existente != null) {
            inventario.setId(id);
            Inventario actualizado = inventarioService.save(inventario);
            log.info("Auditoría - Movimiento de inventario ID: {} actualizado exitosamente", id);
            return ResponseEntity.ok(actualizado);
        }
        
        log.warn("Auditoría - Fallo al actualizar: Movimiento de inventario ID {} no encontrado", id);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        log.warn("Auditoría Crítica - Petición para ELIMINAR movimiento de inventario ID: {}", id);
        
        Inventario inventario = inventarioService.findById(id);
        if (inventario != null) {
            inventarioService.deleteById(id);
            log.info("Auditoría Crítica - Movimiento de inventario ID: {} eliminado exitosamente", id);
            return ResponseEntity.noContent().build();
        }
        
        log.warn("Auditoría - Fallo al eliminar: Movimiento de inventario ID {} no existe", id);
        return ResponseEntity.notFound().build();
    }
}