package com.soap;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Filter;
import jakarta.xml.ws.Endpoint;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.concurrent.Executors;

public class Publicador {

    public static void main(String[] args) {
        try {
            int port = 8081;
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.setExecutor(Executors.newFixedThreadPool(10));

            // Publicar ConversorSOAP
            Endpoint conversorEndpoint = Endpoint.create(new ConversorSOAP());
            HttpContext conversorContext = server.createContext("/calculadora");
            conversorEndpoint.publish(conversorContext);
            conversorContext.getFilters().add(new CorsFilter());

            // Publicar VentaProductosSOAP
            Endpoint ventasEndpoint = Endpoint.create(new VentaProductosSOAP());
            HttpContext ventasContext = server.createContext("/ventas");
            ventasEndpoint.publish(ventasContext);
            ventasContext.getFilters().add(new CorsFilter());

            server.start();

            System.out.println("=== Servidor SOAP Iniciado ===");
            System.out.println("Servicio Conversor de Temperatura: http://localhost:" + port + "/calculadora?wsdl");
            System.out.println("Servicio Venta de Productos: http://localhost:" + port + "/ventas?wsdl");
            System.out.println("Presione Ctrl+C para detener el servidor.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class CorsFilter extends Filter {
        @Override
        public String description() {
            return "CORS Filter to allow browser requests";
        }

        @Override
        public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization, SOAPAction");
            
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            chain.doFilter(exchange);
        }
    }
}
