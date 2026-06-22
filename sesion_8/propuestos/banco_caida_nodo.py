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

def verificar_prepared_transactions(conn, label):
    """Consulta pg_prepared_xacts para mostrar transacciones en estado dudoso."""
    with conn.cursor() as cur:
        cur.execute("""
            SELECT gid, prepared, owner, database
            FROM pg_prepared_xacts
            ORDER BY prepared DESC
        """)
        rows = cur.fetchall()
        print(f"[{label}] Transacciones preparadas (pg_prepared_xacts): {len(rows)}")
        for r in rows:
            print(f"  - GID: {r[0]} | Preparada: {r[1]} | Owner: {r[2]} | DB: {r[3]}")
        return rows

def transferencia_caida_nodo():
    """
    Simulación de caída de nodo usando PREPARE TRANSACTION nativo de PostgreSQL.
    Genera transacciones preparadas visibles en pg_prepared_xacts y en los logs.
    
    REQUISITO: max_prepared_transactions > 0 en PostgreSQL.
    Si está en 0, ejecuta en el contenedor:
        ALTER SYSTEM SET max_prepared_transactions = 10;
        SELECT pg_reload_conf();
    O reinicia el contenedor con:
        command: postgres -c max_prepared_transactions=10
    """
    print("=== SIMULACIÓN: CAÍDA DE NODO (PREPARE TRANSACTION nativo) ===")
    print("Escenario: Cusco se cae después de PREPARE pero antes de COMMIT PREPARED")
    print()

    xid = "txn_lab08_cusco"
    conn_origen = None
    conn_destino = None
    conn_admin = None

    try:
        # FASE 1: PREPARE TRANSACTION
        print("--- FASE 1: PREPARE TRANSACTION ---")
        conn_origen = get_connection('arequipa')
        conn_destino = get_connection('cusco')
        conn_origen.autocommit = False
        conn_destino.autocommit = False

        print(f"[Arequipa] UPDATE cuentas SET saldo = saldo - 25000 WHERE numero_cuenta = 'AQP-001'")
        with conn_origen.cursor() as cur:
            cur.execute(
                "UPDATE cuentas SET saldo = saldo - 25000 WHERE numero_cuenta = 'AQP-001'"
            )
            print(f"[Arequipa] PREPARE TRANSACTION '{xid}'")
            cur.execute(f"PREPARE TRANSACTION '{xid}'")

        print(f"[Cusco]    UPDATE cuentas SET saldo = saldo + 25000 WHERE numero_cuenta = 'CUS-001'")
        with conn_destino.cursor() as cur:
            cur.execute(
                "UPDATE cuentas SET saldo = saldo + 25000 WHERE numero_cuenta = 'CUS-001'"
            )
            print(f"[Cusco]    PREPARE TRANSACTION '{xid}'")
            cur.execute(f"PREPARE TRANSACTION '{xid}'")

        # Verificar transacciones preparadas en ambos nodos
        print("\n--- Verificación: pg_prepared_xacts ---")
        conn_admin_aqp = get_connection('arequipa')
        conn_admin_cus = get_connection('cusco')
        verificar_prepared_transactions(conn_admin_aqp, "Arequipa")
        verificar_prepared_transactions(conn_admin_cus, "Cusco")
        conn_admin_aqp.close()
        conn_admin_cus.close()

        # FASE 2: COMMIT PREPARED
        print("\n--- FASE 2: COMMIT PREPARED ---")
        print(f"[Coordinador] Enviando COMMIT PREPARED '{xid}' a Arequipa...")
        with conn_origen.cursor() as cur:
            cur.execute(f"COMMIT PREPARED '{xid}'")
        print(f"[Arequipa]    COMMIT PREPARED ejecutado")

        print(f"[Coordinador] Enviando COMMIT PREPARED '{xid}' a Cusco...")
        print(f"[Cusco]       ERROR: Connection reset by peer (nodo caído)")
        raise Exception("Cusco node crashed: connection reset by peer")
        # La línea siguiente nunca se ejecuta (simulación)
        with conn_destino.cursor() as cur:
            cur.execute(f"COMMIT PREPARED '{xid}'")

    except Exception as e:
        print(f"\n[ERROR CRÍTICO] {e}")
        print("[ANÁLISIS] Arequipa hizo COMMIT PREPARED, pero Cusco no recibió la orden")
        print("[ESTADO] Arequipa: S/ debitado | Cusco: transacción en estado dudoso")
        print("[INCONSISTENCIA TEMPORAL] Detectada hasta que Cusco se recupere")

        # Mostrar transacción dudosa pendiente en Cusco
        print("\n--- EVIDENCIA: Transacción dudosa en Cusco ---")
        try:
            conn_check = get_connection('cusco')
            verificar_prepared_transactions(conn_check, "Cusco (post-fallo)")
            conn_check.close()
        except Exception as ex:
            print(f"[Cusco] No se puede conectar para verificar: {ex}")

    finally:
        if conn_origen:
            conn_origen.close()
        if conn_destino:
            conn_destino.close()

    print("\n--- RECUPERACIÓN ---")
    print("Al reiniciar, Cusco consulta al coordinador (Arequipa) para resolver")
    print("transacciones en estado de incertidumbre (duda).")
    print("El coordinador indica COMMIT PREPARED, por lo que Cusco reejecuta la operación.")
    print("\n[NOTA] Para limpiar la transacción dudosa en Cusco, ejecutar manualmente:")
    print(f"       COMMIT PREPARED '{xid}'  -- si el coordinador dice COMMIT")
    print(f"       ROLLBACK PREPARED '{xid}' -- si el coordinador dice ROLLBACK")

if __name__ == '__main__':
    transferencia_caida_nodo()
