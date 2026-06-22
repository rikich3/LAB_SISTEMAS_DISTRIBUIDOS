import psycopg2
from psycopg2 import sql

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

def transferir_exitosa(producto='Paracetamol', cantidad=20):
    """
    Ejercicio 1: Transferencia Exitosa
    Transferir 20 unidades desde Arequipa hacia Lima.
    """
    conn_origen = None
    conn_destino = None

    try:
        conn_origen = get_connection('arequipa')
        conn_destino = get_connection('lima')

        # Iniciar transacciones en ambos nodos
        conn_origen.autocommit = False
        conn_destino.autocommit = False

        # 1. Verificar stock disponible en origen
        print(f"[1/5] Verificando stock de '{producto}' en Arequipa...")
        stock_origen = verificar_stock(conn_origen, producto, cantidad)
        print(f"      Stock disponible: {stock_origen}")

        # 2. Iniciar transacción (ya iniciada con autocommit=False)
        print("[2/5] Transacciones iniciadas en ambos nodos")

        # 3. Actualizar inventario origen (descontar)
        print(f"[3/5] Descontando {cantidad} unidades de Arequipa...")
        with conn_origen.cursor() as cur:
            cur.execute(
                "UPDATE inventario SET stock = stock - %s WHERE producto = %s",
                (cantidad, producto)
            )

        # 4. Actualizar inventario destino (incrementar)
        print(f"[4/5] Incrementando {cantidad} unidades en Lima...")
        with conn_destino.cursor() as cur:
            cur.execute(
                "UPDATE inventario SET stock = stock + %s WHERE producto = %s",
                (cantidad, producto)
            )

        # 5. Confirmar cambios (COMMIT en ambos nodos)
        print("[5/5] Confirmando transacción (COMMIT)...")
        conn_origen.commit()
        conn_destino.commit()
        print("[OK] Transacción confirmada exitosamente.")

        # Mostrar resultados finales
        print("\n--- RESULTADOS FINALES ---")
        with get_connection('arequipa').cursor() as cur:
            cur.execute("SELECT producto, stock FROM inventario WHERE producto = %s", (producto,))
            row = cur.fetchone()
            print(f"Arequipa: {row[1]} unidades de {row[0]}")
        with get_connection('lima').cursor() as cur:
            cur.execute("SELECT producto, stock FROM inventario WHERE producto = %s", (producto,))
            row = cur.fetchone()
            print(f"Lima:     {row[1]} unidades de {row[0]}")

    except Exception as e:
        print(f"[ERROR] {e}")
        if conn_origen:
            conn_origen.rollback()
        if conn_destino:
            conn_destino.rollback()
        print("[ROLLBACK] Transacciones revertidas en ambos nodos.")
    finally:
        if conn_origen:
            conn_origen.close()
        if conn_destino:
            conn_destino.close()

if __name__ == '__main__':
    transferir_exitosa()
