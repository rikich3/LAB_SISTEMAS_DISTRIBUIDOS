# Reporte de Implementación y Evaluación: Algoritmos de Sincronización de Relojes

Este reporte describe las tareas realizadas, la implementación y la evaluación de los algoritmos de sincronización de relojes en sistemas distribuidos: **Algoritmo de Cristian** y **Algoritmo de Berkeley**.

## 1. Tareas Realizadas

Como parte de la sesión de laboratorio 2 de Sistemas Distribuidos, se llevaron a cabo las siguientes tareas:
1. **Investigación y Diseño:** Comprensión del funcionamiento interno de ambos algoritmos y cómo simularlos en un entorno local utilizando Java.
2. **Implementación mediante Enfoque TDD:** Se escribieron pruebas predefinidas al inicio de cada clase para asegurar el comportamiento correcto (Test-Driven Development) siguiendo las reglas de calidad y metodologías indicadas.
3. **Desarrollo del Algoritmo de Cristian (`CristianAlgorithm.java`):**
   - Simula un cliente solicitando el tiempo a un servidor.
   - Calcula el _Round-Trip Time (RTT)_ como la diferencia entre la emisión de la solicitud y la recepción de respuesta.
   - Ajusta el reloj del cliente en función del tiempo del servidor más la mitad del RTT.
4. **Desarrollo del Algoritmo de Berkeley (`BerkeleyAlgorithm.java`):**
   - Simula un demonio o nodo maestro encuestando el estado del tiempo de otros nodos de la red.
   - Promedia de manera estática los tiempos recolectados.
   - Retorna un arreglo con los ajustes (deltas) precisos que cada nodo (incluyendo el maestro) debe sumar/restar para sincronizarse exactamente al promedio.
5. **Ejecución y Evaluación:** Se compilaron y ejecutaron los algoritmos, validando a través del registro las salidas (logs).

## 2. Resultados y Evaluación de los Algoritmos

Ambos enfoques superaron de manera estática sus respectivas validaciones internas (pruebas automatizadas embebidas en el método principal).

### 2.1 Resultados: Algoritmo de Cristian
Durante la ejecución:
- **T0 (Envío solicitud cliente):** `1000 ms`
- **T1 (Recepción repuesta cliente):** `1150 ms`
- **Demora de red procesada (Network delay):** `150 ms`
- **Tiempo retornado del Servidor:** `5075 ms`
- **Cálculo RTT Final:** `150 ms`
- **Tiempo Sincronizado Resultante:** `5150 ms`

**Evaluación:**
El algoritmo logró inferir el retraso introducido por la red (RTT / 2, resultando en `75 ms`). Esto permite establecer el reloj local del cliente asumiendo de manera precisa cuánto tiempo transcurrió en el trayecto de vuelta desde el servidor. El enfoque es ideal para tiempos con latencia predecible o que requieran poca sobrecarga en la red.

### 2.2 Resultados: Algoritmo de Berkeley
Durante la ejecución se introdujo un entorno simulado compuesto del servidor y 3 clientes desajustados:
- **Tiempo del maestro (Servidor):** `3000 ms`
- **Tiempo iniciales de clientes:** `[2980, 3015, 3025] ms`
- **Cálculo de ajustes a aplicar:** `[5, 25, -10, -20] ms`
- **Tiempos luego de sincronización:** `[3005, 3005, 3005] ms`

**Evaluación:**
Al calcular el promedio `(3000 + 2980 + 3015 + 3025) / 4 = 3005`, la lógica determinó las distancias exactas que separaban a todos los nodos del equilibrio de la red. Al realizar el ajuste iterativo sobre los relojes, **todos alcanzaron una sincronización total** al milisegundo ideal (`3005 ms`). Berkeley demostró ser un método robusto para un entorno distribuido interno donde no se cuente con un receptor de hora externo y asume de manera colaborativa la noción temporal.

## 3. Conclusión
El uso de TDD y un diseño estricto garantizó implementaciones de alta tolerancia a fallos calculados (las aserciones funcionaron como cortafuegos). Por un lado, el Algoritmo de Cristian nos brinda un modelo más centralizado y simple dependiente de un actor robusto. Por otro, Berkeley resuelve el problema con un enfoque interno en redes locales e interdependientes. La ejecución directa en el espacio de trabajo comprueba que la teoría matemática y de red es correcta.
