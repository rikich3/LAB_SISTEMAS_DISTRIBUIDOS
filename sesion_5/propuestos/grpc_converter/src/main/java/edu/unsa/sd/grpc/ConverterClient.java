package edu.unsa.sd.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ConverterClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        ConverterGrpc.ConverterBlockingStub stub = ConverterGrpc.newBlockingStub(channel);

        test(stub, 25, ConversionType.CELSIUS_TO_FAHRENHEIT, "25 C");
        test(stub, 150, ConversionType.SOLES_TO_DOLARES, "150 PEN");
        test(stub, 42, ConversionType.KILOMETROS_TO_MILLAS, "42 km");

        channel.shutdown();
    }

    private static void test(ConverterGrpc.ConverterBlockingStub stub, double value, ConversionType type, String label) {
        ConvertRequest request = ConvertRequest.newBuilder().setValue(value).setType(type).build();
        ConvertResponse response = stub.convert(request);
        System.out.printf("%s -> %.4f (%s)%n", label, response.getResult(), response.getMessage());
    }
}
