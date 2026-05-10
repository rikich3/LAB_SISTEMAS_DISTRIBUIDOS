package Conversion;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConversorInterface extends Remote {
    public double convertirADolares(double monto) throws RemoteException;
    public double convertirAEuros(double monto) throws RemoteException;
}