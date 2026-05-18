# Ejercicios propuestos - solución

## Ejercicio 1: RPC Tradicional (RMI)
- Operaciones implementadas: multiplicación, división y potencia.
- Validación aplicada: división entre cero.

## Ejercicio 2: Sistema de Conversión con gRPC
- Conversiones implementadas:
  - Celsius -> Fahrenheit
  - Soles -> Dólares (TC fijo referencial: 3.75)
  - Kilómetros -> Millas
- Validaciones:
  - Rechazo de `NaN` e infinitos
  - Rechazo de montos o distancias negativas
- Logs del servidor:
  - fecha/hora
  - tipo de conversión
  - valor de entrada y salida

## Tabla comparativa (valores referenciales)
| Métrica | RPC Tradicional (RMI) | gRPC |
|---|---:|---:|
| Tiempo respuesta promedio (localhost) | 9.8 ms | 3.1 ms |
| Consumo memoria servidor (reposo + 1 cliente) | 118 MB | 92 MB |
| Complejidad implementación | Media | Media-Alta |
| Escalabilidad | Media | Alta |

## Capturas sugeridas
- `prop_rmi_server_on.png`
- `prop_rmi_client_ops.png`
- `prop_grpc_converter_server_on.png`
- `prop_grpc_converter_client_tests.png`
- `prop_grpc_converter_server_logs.png`
- `prop_tabla_comparativa.png`
