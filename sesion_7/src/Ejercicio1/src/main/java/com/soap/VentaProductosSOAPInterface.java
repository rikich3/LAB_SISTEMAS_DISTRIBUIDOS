package com.soap;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import java.util.List;

@WebService
public interface VentaProductosSOAPInterface {
    @WebMethod
    List<Producto> obtenerProductos();

    @WebMethod
    Producto obtenerProductoPorId(@WebParam(name = "id") int id);

    @WebMethod
    String realizarVenta(@WebParam(name = "idProducto") int idProducto, @WebParam(name = "cantidad") int cantidad);
}
