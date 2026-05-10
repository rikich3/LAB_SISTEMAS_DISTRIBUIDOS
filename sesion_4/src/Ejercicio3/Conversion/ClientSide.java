package Conversion;
import java.rmi.Naming;
import java.util.Scanner;

public class ClientSide {
    public static void main(String [] args) throws Exception{
        Scanner sc = new Scanner(System.in);
        ConversorInterface conversorService = (ConversorInterface) Naming.lookup("CONVERSOR_SERVICE");
        
        while(true){
             System.out.println("\nIngresa la opcion para convertir SOLES a:\n" + 
             "1: Dolares\n" +
             "2: Euros\n" +
             "3: Salir\n");
    
            int selection = sc.nextInt();
            if(selection == 3) break;
            
            System.out.print("Ingrese monto en soles: ");
            double amount = sc.nextDouble();
            
            if(selection == 1) {
                System.out.println("Equivalente en dolares: " + conversorService.convertirADolares(amount));
            } else if(selection == 2) {
                System.out.println("Equivalente en euros: " + conversorService.convertirAEuros(amount));
            } else {
                 System.out.println("Opcion invalida.");
            }
        }
        sc.close();
    }
}