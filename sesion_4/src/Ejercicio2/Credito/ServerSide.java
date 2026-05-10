package Credito;
import java.rmi.Naming;

public class ServerSide {
    public static void main(String [] args) throws Exception {
        CreditCardServerImpl server = new CreditCardServerImpl();
        server.registerCard("1234567890", 1000.0);
        server.registerCard("0987654321", 500.0);
        Naming.rebind("CREDIT_CARD_SERVICE", server);
        System.out.println("Credit Card Server is ready.");
    }
}