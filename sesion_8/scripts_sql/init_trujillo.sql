ALTER SYSTEM SET max_prepared_transactions = 10;

CREATE TABLE IF NOT EXISTS cuentas (
    id SERIAL PRIMARY KEY,
    cliente VARCHAR(100) NOT NULL,
    numero_cuenta VARCHAR(20) UNIQUE NOT NULL,
    saldo NUMERIC(15,2) NOT NULL DEFAULT 0.00
);

INSERT INTO cuentas (cliente, numero_cuenta, saldo) VALUES
    ('Cliente Trujillo', 'TRU-001', 80000.00)
ON CONFLICT DO NOTHING;
