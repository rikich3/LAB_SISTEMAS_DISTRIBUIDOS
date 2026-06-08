# Sesión 7: Servicios Web Basados en el Protocolo SOAP

Este documento contiene la explicación detallada de las actividades y ejercicios realizados durante la **Sesión 7**, enfocada en el diseño, publicación y consumo de servicios web utilizando el protocolo **SOAP (Simple Object Access Protocol)** en diferentes lenguajes (Java, Python y JavaScript/HTML).

---

## Estructura del Proyecto en `src/`

La carpeta `src` se organiza de la siguiente manera:

*   **`Ejercicio1/`**: Proyecto en Java (Maven) que contiene la definición, publicación y consumo local de servicios SOAP.
    *   `src/main/java/com/soap/`:
        *   `ConversorSOAPInterface.java` & `ConversorSOAP.java`: Interfaz e implementación del conversor de temperatura (Celsius $\leftrightarrow$ Fahrenheit).
        *   `VentaProductosSOAPInterface.java` & `VentaProductosSOAP.java`: Interfaz e implementación del sistema básico de ventas en línea.
        *   `Producto.java`: Modelo de datos serializable para la transferencia XML.
        *   `Publicador.java`: Servidor HTTP embebido que publica ambos servicios y aplica un filtro CORS personalizado.
        *   `Consumidor.java`: Cliente Java que consume programáticamente los servicios locales.
    *   `pom.xml`: Configuración de dependencias (Jakarta XML WS, JAXB y Metro JAX-WS Runtime).
*   **`Ejercicio2/`**: Cliente en Python.
    *   `client.py`: Script de Python que consume un servicio externo de calculadora utilizando la biblioteca `zeep`.
    *   `requirements.txt`: Dependencias del cliente Python (`zeep`).
*   **`ActividadAdicional/`**: Dashboard interactivo web.
    *   `index.html`: Interfaz web moderna (HTML/JS) que consume los servicios SOAP locales directamente desde el navegador mediante peticiones HTTP POST con sobres XML.

---

## Ejercicio 1: Servidor y Cliente SOAP en Java

### 1. Conversor de Temperatura
Se desarrolló un servicio SOAP básico para la conversión de unidades de temperatura empleando la especificación **JAX-WS** de Jakarta. 

*   **Interfaz (`ConversorSOAPInterface.java`)**:
    Define los métodos expuestos empleando las anotaciones `@WebService` y `@WebMethod`.
*   **Implementación (`ConversorSOAP.java`)**:
    Implementa la lógica matemática de conversión:
    *   Celsius a Fahrenheit: $F = (C \times 9/5) + 32$
    *   Fahrenheit a Celsius: $C = (F - 32) \times 5/9$

### 2. Servicio Avanzado: Ventas de Productos en Línea
Como actividad de diseño, se implementó un servicio SOAP para gestionar ventas de productos:
*   **Modelo `Producto`**: Clase anotada con `@XmlRootElement` para permitir su serialización/deserialización automática a XML.
*   **Lógica de Ventas (`VentaProductosSOAP.java`)**:
    *   Utiliza un `ConcurrentHashMap` para simular un inventario en memoria de forma segura ante concurrencia.
    *   **`obtenerProductos()`**: Retorna la lista de productos disponibles.
    *   **`obtenerProductoPorId(id)`**: Busca un producto por su clave primaria.
    *   **`realizarVenta(idProducto, cantidad)`**: Descuenta de forma sincronizada (`synchronized`) el stock de un producto si existe disponibilidad suficiente, calculando el importe final y retornando un comprobante textual.

### 3. Publicador con Soporte CORS (`Publicador.java`)
Por defecto, los servicios SOAP locales bloquean las peticiones originadas desde navegadores debido a la política de mismo origen (Same-Origin Policy). Para resolver esto en la actividad adicional, el `Publicador.java` levanta un `HttpServer` de Java configurado con un **`CorsFilter`** que:
1.  Inyecta las cabeceras HTTP CORS necesarias: `Access-Control-Allow-Origin: *`, `Access-Control-Allow-Methods` y `Access-Control-Allow-Headers`.
2.  Responde con un código `204 No Content` a las solicitudes de pre-vuelo (`OPTIONS`) del navegador.

---

## Ejercicio 2: Cliente SOAP con Python

Para consumir servicios web SOAP en Python, se empleó la librería **`zeep`**, la cual compila de manera dinámica el contrato WSDL y genera objetos de servicio nativos de Python.

*   **Endpoint consumido**: Un servicio público de calculadora (`http://www.dneonline.com/calculator.asmx?WSDL`).
*   **Funcionamiento**:
    ```python
    from zeep import Client
    client = Client('http://www.dneonline.com/calculator.asmx?WSDL')
    resultado = client.service.Add(5, 8)
    print(resultado) # Imprime 13
    ```
*   **Análisis**: A diferencia de Java, donde típicamente se generan clases locales a partir del WSDL, `zeep` resuelve el esquema XML sobre la marcha, facilitando enormemente el consumo dinámico.

---

## Actividad Adicional: Consumo de SOAP desde Navegador y Limitaciones

Se creó una interfaz web (`index.html`) de alto nivel estético utilizando CSS moderno (modo oscuro, gradientes y componentes tipo tarjeta) para interactuar visualmente con el servidor Java.

### Limitaciones de SOAP en Navegadores Web
A través del análisis y desarrollo de la aplicación web, se determinaron las razones principales por las cuales **SOAP presenta fuertes limitaciones de consumo directo desde el navegador (JavaScript cliente)**:

1.  **Restricciones de CORS (Cross-Origin Resource Sharing)**:
    Los servidores SOAP empresariales heredados rara vez están configurados para responder a peticiones CORS. Como el navegador realiza una solicitud pre-vuelo `OPTIONS` antes del envío del XML, la llamada falla de inmediato a nivel de navegador si el servidor no admite CORS explícitamente.
2.  **Complejidad en la Construcción y Procesamiento de XML**:
    A diferencia de REST (que emplea JSON nativo en JavaScript), SOAP requiere construir manualmente cadenas XML complejas (el sobre SOAP o *SOAP Envelope*) con namespaces específicos:
    ```javascript
    const soapEnvelope = `<?xml version="1.0" encoding="utf-8"?>
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://soap.com/">
       <soapenv:Body>
          <soap:cToF><arg0>30</arg0></soap:cToF>
       </soapenv:Body>
    </soapenv:Envelope>`;
    ```
    Asimismo, la respuesta XML recibida debe ser analizada con `DOMParser()` para extraer los valores usando selectores de etiquetas, lo cual es ineficiente y propenso a errores en comparación con la desestructuración de objetos JSON.
3.  **Seguridad y Exposición de Credenciales**:
    SOAP a menudo utiliza estándares de seguridad complejos como `WS-Security` en sus cabeceras. Implementar firmas digitales XML o colocar nombres de usuario y contraseñas directamente en el JavaScript del lado del cliente expone información crítica al público.
4.  **Sobrecarga de Red (Payload Overhead)**:
    Los mensajes XML de SOAP contienen una cantidad masiva de etiquetas de metadatos repetitivas. En aplicaciones web que requieren alta velocidad y optimización de ancho de banda móvil, transferir XML pesado en lugar de JSON ligero es altamente ineficiente.

---

## Instrucciones para Ejecutar los Componentes

### 1. Iniciar el Servidor SOAP (Java)
Desde el directorio `src/Ejercicio1/`:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.soap.Publicador"
```
Esto publicará los servicios en:
*   Conversor: `http://localhost:8081/calculadora?wsdl`
*   Venta de Productos: `http://localhost:8081/ventas?wsdl`

### 2. Ejecutar Consumidor de Consola (Java)
Con el publicador ejecutándose, abre otra terminal en `src/Ejercicio1/` y corre:
```bash
mvn exec:java -Dexec.mainClass="com.soap.Consumidor"
```

### 3. Ejecutar Cliente Python (Ejercicio 2)
Desde el directorio `src/Ejercicio2/`:
```bash
pip install -r requirements.txt
python client.py
```

### 4. Abrir Dashboard Web (HTML/JS)
Simplemente abre el archivo `src/ActividadAdicional/index.html` en cualquier navegador moderno. Asegúrate de tener el servidor SOAP de Java corriendo para poder probar las conversiones y compras interactivamente.
