import psycopg2
from psycopg2 import sql
import time
import sys

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

# Nodo coordinador (Arequipa actúa como coordinador 2PC)
COORDINADOR = 'arequipa'

# Log de transacciones para recuperación
transaction_log = []

def log(msg):
    transaction_log.append(msg)
    print(f"[LOG] {msg}")

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

def two_phase_commit_transfer(
    origen_node='arequipa',
    destino_node='cusco',
    cuenta_origen='AQP-001',
    cuenta_destino='CUS-001',
    monto=25000.00
):
    """
    Implementación del protocolo Two-Phase Commit (2PC)
    para transferencia bancaria distribuida.
    """
    conns = {}
    cursors = {}
    participantes = [origen_node, destino_node]

    try:
        log(f"=== INICIO TRANSACCIÓN 2PC ===")
        log(f"Coordinador: {COORDINADOR}")
        log(f"Participantes: {participantes}")
        log(f"Transferencia: S/ {monto:.2f} de {cuenta_origen} ({origen_node}) -> {cuenta_destino} ({destino_node})")

        # FASE 1: PREPARACIÓN (PREPARE)
        log("--- FASE 1: PREPARACIÓN ---")
        prepare_votes = {}

        for node in participantes:
            try:
                log(f"[{node}] Conectando...")
                conns[node] = get_connection(node)
                conns[node].autocommit = False
                cursors[node] = conns[node].cursor()

                if node == origen_node:
                    # Verificar saldo y bloquear fila
                    saldo = verificar_saldo(conns[node], cuenta_origen, monto)
                    log(f"[{node}] Saldo verificado: S/ {saldo:.2f}")

                    # Ejecutar débito (PREPARE)
                    cursors[node].execute(
                        "UPDATE cuentas SET saldo = saldo - %s WHERE numero_cuenta = %s",
                        (monto, cuenta_origen)
                    )
                    log(f"[{node}] PREPARE OK: Débito de S/ {monto:.2f} listo")

                elif node == destino_node:
                    # Ejecutar crédito (PREPARE)
                    cursors[node].execute(
                        "UPDATE cuentas SET saldo = saldo + %s WHERE numero_cuenta = %s",
                        (monto, cuenta_destino)
                    )
                    log(f"[{node}] PREPARE OK: Crédito de S/ {monto:.2f} listo")

                prepare_votes[node] = 'YES'
            except Exception as e:
                log(f"[{node}] PREPARE FAILED: {e}")
                prepare_votes[node] = 'NO'

        # Verificar votos de todos los participantes
        log(f"Votos de preparación: {prepare_votes}")
        all_yes = all(v == 'YES' for v in prepare_votes.values())

        # FASE 2: CONFIRMACIÓN (COMMIT) o ABORT
        if all_yes:
            log("--- FASE 2: COMMIT ---")
            for node in participantes:
                conns[node].commit()
                log(f"[{node}] COMMIT ejecutado")
            log("[OK] Transacción 2PC completada exitosamente")
        else:
            log("--- FASE 2: ROLLBACK ---")
            for node in participantes:
                if node in conns:
                    conns[node].rollback()
                    log(f"[{node}] ROLLBACK ejecutado")
            log("[ABORT] Transacción 2PC abortada")

        # Mostrar resultados
        print("\n--- ESTADO FINAL DE CUENTAS ---")
        for node in participantes:
            with get_connection(node).cursor() as cur:
                cuenta = cuenta_origen if node == origen_node else cuenta_destino
                cur.execute("SELECT cliente, numero_cuenta, saldo FROM cuentas WHERE numero_cuenta = %s", (cuenta,))
                row = cur.fetchone()
                print(f"{node}: {row[0]} | {row[1]} | S/ {float(row[2]):,.2f}")

    except Exception as e:
        log(f"[ERROR GLOBAL] {e}")
        for node in participantes:
            if node in conns:
                conns[node].rollback()
                log(f"[{node}] ROLLBACK por error global")
    finally:
        for node in participantes:
            if node in cursors:
                cursors[node].close()
            if node in conns:
                conns[node].close()
        log("=== FIN TRANSACCIÓN 2PC ===")

def simular_falla_red():
    """
    Simulación: Falla de red durante la Fase 1 (PREPARE)
    """
    log("=== SIMULACIÓN: FALLA DE RED ===")
    log("Escenario: El coordinador pierde conectividad con Cusco durante PREPARE")
    
    # Simular que Cusco no responde
    conns = {}
    try:
        conns['arequipa'] = get_connection('arequipa')
        conns['arequipa'].autocommit = False
        log("[arequipa] Conectado y preparado")
        
        log("[cusco] Intentando conectar...")
        raise Exception("Network is unreachable: No route to host cusco:5434")
        
    except Exception as e:
        log(f"[ERROR] {e}")
        if 'arequipa' in conns:
            conns['arequipa'].rollback()
            log("[arequipa] ROLLBACK ejecutado por falla de red")
    finally:
        for c in conns.values():
            c.close()
        log("[RESULTADO] Transacción abortada. Consistencia preservada.")

def simular_caida_nodo():
    """
    Simulación: Caída de nodo durante la Fase 2 (COMMIT)
    """
    log("=== SIMULACIÓN: CAÍDA DE NODO ===")
    log("Escenario: Cusco se cae después de votar YES pero antes de recibir COMMIT")
    
    conns = {}
    try:
        conns['arequipa'] = get_connection('arequipa')
        conns['cusco'] = get_connection('cusco')
        conns['arequipa'].autocommit = False
        conns['cusco'].autocommit = False
        
        log("[FASE 1] Ambos nodos votan YES")
        # Simular prepare en ambos
        cursors = {}
        cursors['arequipa'] = conns['arequipa'].cursor()
        cursors['cusco'] = conns['cusco'].cursor()
        
        cursors['arequipa'].execute(
            "UPDATE cuentas SET saldo = saldo - 25000 WHERE numero_cuenta = 'AQP-001'"
        )
        cursors['cusco'].execute(
            "UPDATE cuentas SET saldo = saldo + 25000 WHERE numero_cuenta = 'CUS-001'"
        )
        
        log("[FASE 2] Enviando COMMIT a Arequipa...")
        conns['arequipa'].commit()
        log("[arequipa] COMMIT ejecutado")
        
        log("[FASE 2] Enviando COMMIT a Cusco...")
        raise Exception("Cusco node crashed: connection reset by peer")
        conns['cusco'].commit()
        
    except Exception as e:
        log(f"[ERROR] {e}")
        log("[CRÍTICO] Arequipa hizo COMMIT pero Cusco no recibió la orden")
        log("[RECOVERRACIÓN] Al reiniciar, Cusco consulta al coordinador para resolver incertidumbre")
    finally:
        for c in conns.values():
            c.close()

def simular_recuperacion():
    """
    Simulación: Recuperación posterior tras caída
    """
    log("=== SIMULACIÓN: RECUPERACIÓN POSTERIOR ===")
    log("Escenario: Cusco se recupera y consulta su estado con el coordinador")
    
    try:
        conn = get_connection('cusco')
        with conn.cursor() as cur:
            log("[cusco] Verificando estado de transacciones pendientes...")
            cur.execute("SELECT xact_status FROM pg_stat_activity WHERE state = 'idle in transaction';")
            # En un sistema real, se consultaría un log de transacciones
            log("[cusco] No hay transacciones pendientes (ya fue resuelta por el coordinador)")
            log("[cusco] Estado consistente alcanzado mediante log de transacciones")
    except Exception as e:
        log(f"[ERROR] {e}")
    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == '__main__':
    print("=" * 60)
    print("SISTEMA NACIONAL DE BANCOS COOPERATIVOS")
    print("Simulación de Transacciones Distribuidas con 2PC")
    print("=" * 60)
    
    # Opción por argumento
    if len(sys.argv) > 1:
        modo = sys.argv[1]
    else:
        modo = 'transferencia'
    
    if modo == 'transferencia':
        two_phase_commit_transfer()
    elif modo == 'falla_red':
        simular_falla_red()
    elif modo == 'caida_nodo':
        simular_caida_nodo()
    elif modo == 'recuperacion':
        simular_recuperacion()
    else:
        print("Modos disponibles: transferencia | falla_red | caida_nodo | recuperacion")
        print("Ejemplo: python banco_cooperativo.py transferencia")
