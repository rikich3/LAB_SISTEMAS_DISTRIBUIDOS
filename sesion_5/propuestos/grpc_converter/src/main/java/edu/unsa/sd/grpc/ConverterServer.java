package edu.unsa.sd.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ConverterServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50052)
                .addService(new ConverterService())
                .build()
                .start();

        System.out.println("Servidor gRPC Converter activo en puerto 50052");
        server.awaitTermination();
    }
}
