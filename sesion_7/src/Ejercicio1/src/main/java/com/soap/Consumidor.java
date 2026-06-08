package com.soap;

import jakarta.xml.ws.Service;
import java.net.URL;
import javax.xml.namespace.QName;

public class Consumidor {
    public static void main(String[] args) {
        try {
            // Consumir ConversorSOAP
            URL wsdlURL = new URL("http://localhost:8081/calculadora?wsdl");
            QName qname = new QName("http://soap.com/", "ConversorSOAPService");
            Service service = Service.create(wsdlURL, qname);
            ConversorSOAPInterface calc = service.getPort(ConversorSOAPInterface.class);
            
            System.out.println("=== Probando ConversorSOAP desde Consumidor Java ===");
            System.out.println("cToF(30) -> Esperado: 86.0 | Resultado: " + calc.cToF(30));
            System.out.println("fToC(86) -> Esperado: 30.0 | Resultado: " + calc.fToC(86));

            // Consumir VentaProductosSOAP
            URL wsdlVentasURL = new URL("http://localhost:8081/ventas?wsdl");
            QName qnameVentas = new QName("http://soap.com/", "VentaProductosSOAPService");
            Service serviceVentas = Service.create(wsdlVentasURL, qnameVentas);
            VentaProductosSOAPInterface ventas = serviceVentas.getPort(VentaProductosSOAPInterface.class);

            System.out.println("\n=== Probando VentaProductosSOAP desde Consumidor Java ===");
            System.out.println("Productos disponibles:");
            for (Producto p : ventas.obtenerProductos()) {
                System.out.printf("- ID: %d | Nombre: %s | Precio: $%.2f | Stock: %d\n",
                        p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
            }

            System.out.println("\nRealizando compra de Laptop (ID: 1, Cantidad: 2):");
            String ticket = ventas.realizarVenta(1, 2);
            System.out.println(ticket);

            System.out.println("\nStock actualizado de Laptop:");
            Producto p = ventas.obtenerProductoPorId(1);
            System.out.printf("- ID: %d | Nombre: %s | Stock actual: %d\n", p.getId(), p.getNombre(), p.getStock());

        } catch (Exception e) {
            System.err.println("Error al conectar con el servicio SOAP. ¿Esta corriendo el Publicador?");
            e.printStackTrace();
        }
    }
}
