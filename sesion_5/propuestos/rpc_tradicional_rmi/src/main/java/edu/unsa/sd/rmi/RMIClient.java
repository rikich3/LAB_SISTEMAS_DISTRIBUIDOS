package edu.unsa.sd.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        Calculator calculator = (Calculator) registry.lookup("CalculatorService");

        System.out.println("Multiplicacion 6 * 7 = " + calculator.multiply(6, 7));
        System.out.println("Division 22 / 7 = " + calculator.divide(22, 7));
        System.out.println("Potencia 2 ^ 10 = " + calculator.power(2, 10));
    }
}
