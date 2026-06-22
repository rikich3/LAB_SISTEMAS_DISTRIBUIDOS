import psycopg2

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
    }
}

def get_connection(node):
    config = DB_CONFIG[node]
    return psycopg2.connect(**config)

def recuperacion_posterior():
    """
    Simulación de recuperación posterior tras caída de nodo.
    Cusco se recupera y consulta al coordinador para resolver transacciones dudosas.
    """
    print("=== SIMULACIÓN: RECUPERACIÓN POSTERIOR ===")
    print("Escenario: Cusco se recupera tras caída y consulta estado de transacciones")
    print()

    try:
        # Simular que Cusco se recupera y consulta al coordinador
        print("[Cusco]  Iniciando protocolo de recuperación...")
        print("[Cusco]  Consultando log de transacciones locales...")
        print("[Cusco]  Transacción T-123 encontrada en estado 'duda' (PREPARE recibido, COMMIT no confirmado)")
        print()
        print("[Cusco]  Contactando coordinador (Arequipa)...")
        print("[Arequipa] Consultando estado de transacción T-123 en log global...")
        print("[Arequipa] Resultado: COMMIT confirmado")
        print()
        print("[Cusco]  Reejecutando COMMIT para transacción T-123")
        
        conn = get_connection('cusco')
        conn.autocommit = False
        with conn.cursor() as cur:
            cur.execute(
                "UPDATE cuentas SET saldo = saldo + 25000 WHERE numero_cuenta = 'CUS-001'"
            )
        conn.commit()
        print("[Cusco]  COMMIT reejecutado exitosamente")
        conn.close()

        print("\n[RESULTADO] Estado consistente restaurado en ambos nodos")
        print("[MECANISMO] Log de transacciones (write-ahead log) permite recuperación")

    except Exception as e:
        print(f"[ERROR] {e}")
    finally:
        print("\n--- ESTADO FINAL TRAS RECUPERACIÓN ---")
        with get_connection('arequipa').cursor() as cur:
            cur.execute("SELECT numero_cuenta, saldo FROM cuentas WHERE numero_cuenta = 'AQP-001'")
            row = cur.fetchone()
            print(f"Arequipa: {row[0]} | S/ {float(row[1]):,.2f}")
        with get_connection('cusco').cursor() as cur:
            cur.execute("SELECT numero_cuenta, saldo FROM cuentas WHERE numero_cuenta = 'CUS-001'")
            row = cur.fetchone()
            print(f"Cusco:    {row[0]} | S/ {float(row[1]):,.2f}")

if __name__ == '__main__':
    recuperacion_posterior()
