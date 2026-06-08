package com.soap;

import jakarta.jws.WebService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@WebService(endpointInterface = "com.soap.VentaProductosSOAPInterface")
public class VentaProductosSOAP implements VentaProductosSOAPInterface {
    private static final ConcurrentHashMap<Integer, Producto> inventario = new ConcurrentHashMap<>();

    static {
        inventario.put(1, new Producto(1, "Laptop", 899.99, 10));
        inventario.put(2, new Producto(2, "Mouse Inalambrico", 25.50, 50));
        inventario.put(3, new Producto(3, "Teclado Mecanico", 75.00, 30));
        inventario.put(4, new Producto(4, "Monitor 4K", 349.99, 15));
    }

    @Override
    public List<Producto> obtenerProductos() {
        return new ArrayList<>(inventario.values());
    }

    @Override
    public Producto obtenerProductoPorId(int id) {
        return inventario.get(id);
    }

    @Override
    public String realizarVenta(int idProducto, int cantidad) {
        Producto prod = inventario.get(idProducto);
        if (prod == null) {
            return "Error: Producto no encontrado.";
        }
        synchronized (prod) {
            if (prod.getStock() < cantidad) {
                return "Error: Stock insuficiente. Stock disponible: " + prod.getStock();
            }
            prod.setStock(prod.getStock() - cantidad);
            double total = prod.getPrecio() * cantidad;
            return String.format("Venta exitosa: %d unidades de '%s' compradas. Total: $%.2f. Stock restante: %d",
                    cantidad, prod.getNombre(), total, prod.getStock());
        }
    }
}
