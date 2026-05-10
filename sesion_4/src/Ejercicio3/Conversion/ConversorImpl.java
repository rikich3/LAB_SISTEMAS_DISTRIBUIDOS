package Conversion;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ConversorImpl extends UnicastRemoteObject implements ConversorInterface{
    private final double TASA_DOLAR = 0.27; // 1 Sol = 0.27 USD
    private final double TASA_EURO = 0.25;  // 1 Sol = 0.25 EUR
    
    public ConversorImpl() throws RemoteException {
        super();
    }
    
    @Override
    public double convertirADolares(double monto) throws RemoteException {
        return monto * TASA_DOLAR;
    }
    
    @Override
    public double convertirAEuros(double monto) throws RemoteException {
        return monto * TASA_EURO;
    }
}