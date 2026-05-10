#!/bin/bash

# Asegurarse de que corra desde la carpeta sesion_4
# Cerrar cualquier instancia previa de rmiregistry pegada en el puerto 1099
echo "[+] Limpiando procesos rmiregistry antiguos..."
killall rmiregistry 2>/dev/null

echo "=================================================="
echo "    EJECUTOR DE EJERCICIOS RMI - SESION 4         "
echo "=================================================="
echo "Seleccione un ejercicio para iniciar:"
echo "  1 - Ejercicio 1: Farmacia (Medicinas)"
echo "  2 - Ejercicio 2: Tarjetas de Credito"
echo "  3 - Ejercicio 3: Conversor de Moneda"
echo "=================================================="
read -p "Ingrese el numero (1, 2 o 3): " NUM

case $NUM in
    1)
        DIR="src/Ejercicio1"
        PACKAGE="Medicinas"
        SERVER="ServerSide"
        CLIENT="ClienteSide"
        ;;
    2)
        DIR="src/Ejercicio2"
        PACKAGE="Credito"
        SERVER="ServerSide"
        CLIENT="ClientSide"
        ;;
    3)
        DIR="src/Ejercicio3"
        PACKAGE="Conversion"
        SERVER="ServerSide"
        CLIENT="ClientSide"
        ;;
    *)
        echo "[-] Opcion invalida. Saliendo."
        exit 1
        ;;
esac

cd "$DIR" || { echo "[-] No se pudo acceder a la carpeta $DIR"; exit 1; }

echo "[+] Compilando clases del $PACKAGE..."
javac ${PACKAGE}/*.java

echo "[+] Iniciando rmiregistry en segundo plano..."
rmiregistry -J-Djava.class.path=./ &
RMI_PID=$!

# Pausa para asegurar que rmiregistry este levantado y escuchando el puerto.
sleep 2

echo "[+] Iniciando $PACKAGE.$SERVER en segundo plano..."
java -cp ./ ${PACKAGE}.${SERVER} &
SERVER_PID=$!

# Pausa para que el servidor complete el registro de sus objetos en el rmiregistry.
sleep 1

echo "=================================================="
echo " Iniciando Cliente interactivo... (Presiona Ctrl+C o sal mediante sus opciones para terminar)"
echo "=================================================="

# Ejecutar el cliente. Bloqueara el script hasta su terminacion.
java -cp ./ ${PACKAGE}.${CLIENT}

echo "=================================================="
echo "[+] Cliente desconectado. Limpiando entorno..."

# Matar los procesos guardados
kill $SERVER_PID 2>/dev/null
kill $RMI_PID 2>/dev/null

echo "[+] Todo limpio. Adios!"
