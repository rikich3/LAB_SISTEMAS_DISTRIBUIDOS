package Credito;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CreditCardInterface extends Remote {
    public boolean processPayment(String cardNumber, double amount) throws RemoteException, CreditException;
    public double checkBalance(String cardNumber) throws RemoteException, CreditException;
    public void registerCard(String cardNumber, double limit) throws RemoteException;
}