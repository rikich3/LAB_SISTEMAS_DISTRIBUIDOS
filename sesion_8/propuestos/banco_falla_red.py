import psycopg2
from psycopg2 import sql
import time

# Configuración de los nodos del banco cooperativo
DB_CONFIG = {
    'arequipa': {
        'host': 'localhost',
        'port': 5432,
        'dbname': 'banco_arequipa',
        'user': 'admin',
        'password': 'admin123'
    },
    'cusco': {
        'host': 'localhost',
        'port': 5434,
        'dbname': 'banco_cusco',
        'user': 'admin',
        'password': 'admin123'
    },
    'trujillo': {
        'host': 'localhost',
        'port': 5435,
        'dbname': 'banco_trujillo',
        'user': 'admin',
        'password': 'admin123'
    }
}

def get_connection(node):
    config = DB_CONFIG[node]
    return psycopg2.connect(**config)

def verificar_saldo(conn, cuenta, monto):
    with conn.cursor() as cur:
        cur.execute(
            "SELECT saldo FROM cuentas WHERE numero_cuenta = %s FOR UPDATE",
            (cuenta,)
        )
        row = cur.fetchone()
        if not row:
            raise Exception(f"Cuenta {cuenta} no encontrada")
        saldo = float(row[0])
        if saldo < monto:
            raise Exception(f"Saldo insuficiente: S/ {saldo:.2f} < S/ {monto:.2f}")
        return saldo

def transferencia_falla_red():
    """
    Simulación de falla de red durante la transferencia.
    El coordinador (Arequipa) pierde conectividad con Cusco.
    """
    print("=== SIMULACIÓN: FALLA DE RED ===")
    print("Escenario: Durante la Fase 1 (PREPARE), el coordinador no puede contactar a Cusco")
    print()

    conn_origen = None
    conn_destino = None

    try:
        # 1. Conectar a Arequipa (coordinador)
        print("[PASO 1] Conectando a Arequipa (coordinador)...")
        conn_origen = get_connection('arequipa')
        conn_origen.autocommit = False
        print("         Arequipa conectado.")

        # 2. Intentar conectar a Cusco (simulando falla de red)
        print("[PASO 2] Intentando conectar a Cusco...")
        print("         ERROR: Network is unreachable (simulado)")
        raise Exception("Falla de red: No se puede establecer conexión con el nodo Cusco")

        # Este código no se ejecuta
        conn_destino = get_connection('cusco')
        conn_destino.autocommit = False

    except Exception as e:
        print(f"[ERROR] {e}")
        if conn_origen:
            conn_origen.rollback()
            print("[ROLLBACK] Arequipa: transacción revertida")
        print("[RESULTADO] La transacción se aborta. No se realizan cambios.")
        print("[IMPACTO] Consistencia preservada: saldos no modificados.")
    finally:
        if conn_origen:
            conn_origen.close()
        if conn_destino:
            conn_destino.close()

    # Verificar estado final
    print("\n--- ESTADO FINAL DE CUENTAS ---")
    with get_connection('arequipa').cursor() as cur:
        cur.execute("SELECT numero_cuenta, saldo FROM cuentas WHERE numero_cuenta = 'AQP-001'")
        row = cur.fetchone()
        print(f"Arequipa: {row[0]} | S/ {float(row[1]):,.2f}")
    with get_connection('cusco').cursor() as cur:
        cur.execute("SELECT numero_cuenta, saldo FROM cuentas WHERE numero_cuenta = 'CUS-001'")
        row = cur.fetchone()
        print(f"Cusco:    {row[0]} | S/ {float(row[1]):,.2f}")

if __name__ == '__main__':
    transferencia_falla_red()
