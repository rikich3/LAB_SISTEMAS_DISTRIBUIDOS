package Credito;
import java.rmi.Naming;
import java.util.Scanner;

public class ClientSide {
    public static void main(String [] args) throws Exception{
        Scanner sc = new Scanner(System.in);
        CreditCardInterface creditService = (CreditCardInterface) Naming.lookup("CREDIT_CARD_SERVICE");
        
        while(true){
             System.out.println("\nIngresa la opcion\n" + 
             "1: Consultar Saldo\n" +
             "2: Realizar Pago\n" +
             "3: Salir\n");
    
            int selection = sc.nextInt();
            if(selection == 3) break;
            
            System.out.print("Ingrese numero de tarjeta: ");
            String card = sc.next();
            
            try{
                if(selection == 1) {
                    System.out.println("Saldo disponible: " + creditService.checkBalance(card));
                } else if(selection == 2) {
                    System.out.print("Ingrese monto a pagar: ");
                    double amount = sc.nextDouble();
                    if(creditService.processPayment(card, amount)){
                        System.out.println("Pago realizado con exito.");
                    }else{
                        System.out.println("Saldo insuficiente.");
                    }
                } else {
                     System.out.println("Opcion invalida.");
                }
            }catch(CreditException e){
                System.out.println("Error: " + e.getMessage());
            }
        }
        sc.close();
    }
}