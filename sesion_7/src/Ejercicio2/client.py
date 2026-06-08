from zeep import Client

# URL del WSDL del servicio de calculadora existente
wsdl_url = 'http://www.dneonline.com/calculator.asmx?WSDL'

try:
    print(f"Conectando al servicio SOAP en: {wsdl_url}...")
    client = Client(wsdl_url)

    # Consumir el metodo Add
    a, b = 5, 8
    print(f"Invocando Add({a}, {b})...")
    resultado = client.service.Add(a, b)

    print("\n=== Resultado Esperado: 13 ===")
    print(f"Resultado Obtenido: {resultado}")

except Exception as e:
    print("Ocurrio un error al intentar consumir el servicio SOAP:")
    print(e)
