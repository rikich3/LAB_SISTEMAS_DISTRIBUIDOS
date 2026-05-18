package edu.unsa.sd.resuelto;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CalculatorServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051)
                .addService(new CalculatorService())
                .build()
                .start();

        System.out.println("Servidor gRPC Calculator iniciado en puerto 50051");
        server.awaitTermination();
    }
}
