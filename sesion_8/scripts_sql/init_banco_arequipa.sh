#!/bin/bash
set -e

# Habilitar transacciones preparadas (requerido para 2PC / PREPARE TRANSACTION)
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    ALTER SYSTEM SET max_prepared_transactions = 10;
EOSQL

# Crear base de datos banco_arequipa si no existe
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE banco_arequipa;
EOSQL

# Crear tabla cuentas en banco_arequipa
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "banco_arequipa" <<-EOSQL
    CREATE TABLE IF NOT EXISTS cuentas (
        id SERIAL PRIMARY KEY,
        cliente VARCHAR(100) NOT NULL,
        numero_cuenta VARCHAR(20) UNIQUE NOT NULL,
        saldo NUMERIC(15,2) NOT NULL DEFAULT 0.00
    );

    INSERT INTO cuentas (cliente, numero_cuenta, saldo) VALUES
        ('Cliente Arequipa', 'AQP-001', 100000.00)
    ON CONFLICT DO NOTHING;
EOSQL
