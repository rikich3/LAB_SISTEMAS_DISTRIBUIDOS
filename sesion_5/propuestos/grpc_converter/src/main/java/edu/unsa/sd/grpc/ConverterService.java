package edu.unsa.sd.grpc;

import io.grpc.stub.StreamObserver;
import java.time.LocalDateTime;

public class ConverterService extends ConverterGrpc.ConverterImplBase {
    private static final double TC_SOLES_DOLAR = 3.75;

    @Override
    public void convert(ConvertRequest req, StreamObserver<ConvertResponse> responseObserver) {
        double value = req.getValue();
        ConversionType type = req.getType();

        if (Double.isNaN(value) || Double.isInfinite(value)) {
            responseObserver.onError(new IllegalArgumentException("Valor no valido"));
            return;
        }

        double result;
        String msg;

        switch (type) {
            case CELSIUS_TO_FAHRENHEIT:
                result = (value * 1.8) + 32;
                msg = "Conversion de temperatura";
                break;
            case SOLES_TO_DOLARES:
                if (value < 0) {
                    responseObserver.onError(new IllegalArgumentException("Monto en soles no puede ser negativo"));
                    return;
                }
                result = value / TC_SOLES_DOLAR;
                msg = "Conversion de moneda";
                break;
            case KILOMETROS_TO_MILLAS:
                if (value < 0) {
                    responseObserver.onError(new IllegalArgumentException("Distancia no puede ser negativa"));
                    return;
                }
                result = value * 0.621371;
                msg = "Conversion de distancia";
                break;
            default:
                responseObserver.onError(new IllegalArgumentException("Tipo de conversion no soportado"));
                return;
        }

        System.out.printf("[%s] %s: entrada=%.4f salida=%.4f%n", LocalDateTime.now(), type, value, result);

        responseObserver.onNext(ConvertResponse.newBuilder().setResult(result).setMessage(msg).build());
        responseObserver.onCompleted();
    }
}
