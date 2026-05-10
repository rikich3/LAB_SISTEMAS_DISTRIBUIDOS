package Credito;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class CreditCardServerImpl extends UnicastRemoteObject implements CreditCardInterface{
    private HashMap<String, Double> cards = new HashMap<>();
    
    public CreditCardServerImpl() throws RemoteException {
        super();
    }

    public void registerCard(String cardNumber, double limit) throws RemoteException{
        cards.put(cardNumber, limit);
    }
    
    @Override
    public boolean processPayment(String cardNumber, double amount) throws RemoteException, CreditException{
        if(amount <= 0){
            throw new CreditException("Payment amount must be positive");
        }
        if(!cards.containsKey(cardNumber)){
            throw new CreditException("Invalid card number");
        }
        double currentLimit = cards.get(cardNumber);
        if(currentLimit >= amount) {
            cards.put(cardNumber, currentLimit - amount);
            return true;
        }
        return false;
    }
    
    @Override
    public double checkBalance(String cardNumber) throws RemoteException, CreditException {
         if(!cards.containsKey(cardNumber)){
            throw new CreditException("Invalid card number");
        }
        return cards.get(cardNumber);
    }
}