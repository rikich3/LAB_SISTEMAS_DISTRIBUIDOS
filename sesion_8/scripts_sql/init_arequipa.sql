ALTER SYSTEM SET max_prepared_transactions = 10;

CREATE TABLE IF NOT EXISTS inventario (
    id SERIAL PRIMARY KEY,
    producto VARCHAR(100) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0
);

INSERT INTO inventario (producto, stock) VALUES
    ('Paracetamol', 100)
ON CONFLICT DO NOTHING;
