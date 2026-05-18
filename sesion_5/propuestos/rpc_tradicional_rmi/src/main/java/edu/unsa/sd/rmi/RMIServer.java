package edu.unsa.sd.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("CalculatorService", new CalculatorImpl());
        System.out.println("Servidor RMI activo en puerto 1099");
    }
}
