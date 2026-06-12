package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Venta;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Métodos obligatorios requeridos por la interfaz VentaService
    @Override
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta findById(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Venta> findByUsuarioId(Long usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId);
    }

    // Lógica robusta del método save requerida para pasar las pruebas unitarias de la S4
    @Override
    @Transactional // Asegura la atomicidad: si falla el stock de un producto, la venta completa se cancela
    public Venta save(Venta venta) {
        // 1. Validar que la venta tenga un usuario asignado
        if (venta.getUsuario() == null || venta.getUsuario().getId() == null) {
            throw new RuntimeException("La venta debe estar vinculada a un usuario válido");
        }

        // 2. Validar stock y precios en los detalles
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Validar si hay unidades suficientes en el inventario
            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            // Descontar las unidades compradas del stock
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            // Sincronizar el precio unitario del detalle con el precio actual del producto en catálogo
            detalle.setPrecio(producto.getPrecio());
            
            // Vincular bidireccionalmente la relación JPA
            detalle.setVenta(venta);
        }

        return ventaRepository.save(venta);
    }
}