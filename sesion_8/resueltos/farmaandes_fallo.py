import psycopg2

# Configuración de conexiones
DB_CONFIG = {
    'arequipa': {
        'host': 'localhost',
        'port': 5432,
        'dbname': 'almacen_arequipa',
        'user': 'admin',
        'password': 'admin123'
    },
    'lima': {
        'host': 'localhost',
        'port': 5433,
        'dbname': 'almacen_lima',
        'user': 'admin',
        'password': 'admin123'
    }
}

def get_connection(node):
    config = DB_CONFIG[node]
    return psycopg2.connect(**config)

def verificar_stock(conn, producto, minimo):
    with conn.cursor() as cur:
        cur.execute(
            "SELECT stock FROM inventario WHERE producto = %s FOR UPDATE",
            (producto,)
        )
        row = cur.fetchone()
        if not row:
            raise Exception(f"Producto '{producto}' no encontrado")
        stock = row[0]
        if stock < minimo:
            raise Exception(f"Stock insuficiente: {stock} < {minimo}")
        return stock

def transferir_con_fallo(producto='Paracetamol', cantidad=20):
    """
    Ejercicio 2: Simulación de Fallo
    Durante la transferencia, el nodo Lima deja de responder.
    Se debe ejecutar rollback.
    """
    conn_origen = None
    conn_destino = None

    try:
        conn_origen = get_connection('arequipa')
        conn_destino = get_connection('lima')

        conn_origen.autocommit = False
        conn_destino.autocommit = False

        # 1. Iniciar transacción
        print("[1/4] Transacciones iniciadas en ambos nodos")

        # 2. Descontar stock en Arequipa
        print(f"[2/4] Descontando {cantidad} unidades de Arequipa...")
        with conn_origen.cursor() as cur:
            cur.execute(
                "UPDATE inventario SET stock = stock - %s WHERE producto = %s",
                (cantidad, producto)
            )

        # 3. Simular caída de Lima (lanzar excepción antes de actualizar destino)
        print("[3/4] Simulando caída de nodo Lima...")
        raise Exception("NODO LIMA NO RESPONDE: Connection timed out")

        # Este código nunca se ejecutará debido a la excepción anterior
        print(f"[4/4] Incrementando {cantidad} unidades en Lima...")
        with conn_destino.cursor() as cur:
            cur.execute(
                "UPDATE inventario SET stock = stock + %s WHERE producto = %s",
                (cantidad, producto)
            )

        # Confirmar (nunca se alcanza)
        conn_origen.commit()
        conn_destino.commit()
        print("[OK] Transacción confirmada.")

    except Exception as e:
        print(f"[ERROR] {e}")
        if conn_origen:
            conn_origen.rollback()
            print("[ROLLBACK] Arequipa: transacción revertida.")
        if conn_destino:
            conn_destino.rollback()
            print("[ROLLBACK] Lima: transacción revertida.")
        print("[RESULTADO] Ambos nodos mantienen stock original.")
    finally:
        if conn_origen:
            conn_origen.close()
        if conn_destino:
            conn_destino.close()

        # Mostrar estado final (debe ser igual al inicial)
        print("\n--- ESTADO FINAL TRAS ROLLBACK ---")
        with get_connection('arequipa').cursor() as cur:
            cur.execute("SELECT producto, stock FROM inventario WHERE producto = %s", (producto,))
            row = cur.fetchone()
            print(f"Arequipa: {row[1]} unidades de {row[0]} (esperado: 100)")
        with get_connection('lima').cursor() as cur:
            cur.execute("SELECT producto, stock FROM inventario WHERE producto = %s", (producto,))
            row = cur.fetchone()
            print(f"Lima:     {row[1]} unidades de {row[0]} (esperado: 50)")

if __name__ == '__main__':
    transferir_con_fallo()
