package Conversion;
import java.rmi.Naming;

public class ServerSide {
     public static void main(String [] args) throws Exception {
        ConversorImpl server = new ConversorImpl();
        Naming.rebind("CONVERSOR_SERVICE", server);
        System.out.println("Conversor Server is ready.");
    }
}