# Laboratorio 08: Bases de Datos Distribuidas

## Información General

- **Asignatura:** Sistemas Distribuidos
- **Práctica:** 08
- **Tema:** Bases de Datos Distribuidas
- **Grupo:** B
- **Integrantes:**
  - Chambilla Perca Ricardo Mauricio
  - Gutierrez Ccama Juan Diego
  - Valdivia Segovia Ryan Fabian
- **Docente:** Mg. Maribel Molina Barriga

## Estructura del Repositorio

```
Laboratorio08/
├── docker-compose.yml          # Orquestación de PostgreSQL multi-nodo
├── scripts_sql/                # Scripts de inicialización
│   ├── init_arequipa.sql
│   ├── init_lima.sql
│   ├── init_cusco.sql
│   └── init_trujillo.sql
├── resueltos/                  # Ejercicios resueltos (FarmaAndes)
│   ├── farmaandes_transferencia.py
│   └── farmaandes_fallo.py
├── propuestos/                 # Ejercicios propuestos (Banco Cooperativo)
│   ├── banco_cooperativo.py
│   ├── banco_falla_red.py
│   ├── banco_caida_nodo.py
│   └── banco_recuperacion.py
├── informe/
│   ├── main.tex                # Informe LaTeX
│   └── img/                    # Capturas de pantalla (placeholder)
└── README.md                   # Este archivo
```

## Requisitos

- Docker Desktop
- Docker Compose
- Python 3.12
- Librerías Python:
  ```bash
  pip install psycopg2-binary
  ```
- PostgreSQL 16 (opcional, si se usa localmente sin Docker)
- pgAdmin 4 o DBeaver (para visualizar bases de datos)

## Instrucciones de Ejecución

### 1. Levantar los nodos PostgreSQL

```bash
docker-compose up -d
```

Esto crea 4 contenedores:

- `almacen_arequipa` → puerto 5432
- `almacen_lima` → puerto 5433
- `banco_cusco` → puerto 5434
- `banco_trujillo` → puerto 5435

### 2. Verificar contenedores en ejecución

```bash
docker ps
```

### 3. Ejecutar ejercicios resueltos (FarmaAndes)

#### Transferencia exitosa

```bash
cd resueltos
python farmaandes_transferencia.py
```

**Resultado esperado:**

- Arequipa: 80 unidades
- Lima: 70 unidades

#### Simulación de fallo

```bash
cd resueltos
python farmaandes_fallo.py
```

**Resultado esperado:**

- Arequipa: 100 unidades (sin cambios)
- Lima: 50 unidades (sin cambios)

### 4. Ejecutar ejercicios propuestos (Banco Cooperativo)

#### Transferencia 2PC exitosa

```bash
cd propuestos
python banco_cooperativo.py transferencia
```

#### Simulación de falla de red

```bash
cd propuestos
python banco_falla_red.py
```

#### Simulación de caída de nodo

```bash
cd propuestos
python banco_caida_nodo.py
```

#### Simulación de recuperación

```bash
cd propuestos
python banco_recuperacion.py
```

### 5. Conectar con pgAdmin / DBeaver

- Host: `localhost`
- Puertos: `5432`, `5433`, `5434`, `5435`
- Usuario: `admin`
- Contraseña: `admin123`
- Bases de datos: `almacen_arequipa`, `almacen_lima`, `banco_cusco`, `banco_trujillo`

## Capturas de Pantalla Requeridas

### Resueltos

1. `docker_contenedores_up.png` — Contenedores Docker en ejecución
2. `resuelto_transferencia_terminal.png` — Salida de transferencia exitosa
3. `resuelto_transferencia_arequipa.png` — Stock Arequipa = 80 en pgAdmin
4. `resuelto_transferencia_lima.png` — Stock Lima = 70 en pgAdmin
5. `resuelto_fallo_terminal.png` — Salida de simulación de fallo
6. `resuelto_fallo_arequipa.png` — Stock Arequipa = 100 (sin cambios)
7. `resuelto_fallo_lima.png` — Stock Lima = 50 (sin cambios)

### Propuestos

8. `prop_arquitectura.png` — Diagrama de arquitectura del banco
9. `prop_diagrama_secuencia_ok.png` — Diagrama de secuencia 2PC exitoso
10. `prop_diagrama_secuencia_fallo.png` — Diagrama de secuencia 2PC con fallo
11. `prop_transferencia_terminal.png` — Ejecución de transferencia bancaria 2PC
12. `prop_saldo_arequipa_dbeaver.png` — Saldo Arequipa tras 2PC
13. `prop_saldo_cusco_dbeaver.png` — Saldo Cusco tras 2PC
14. `prop_falla_red_terminal.png` — Ejecución de falla de red
15. `prop_falla_red_saldos.png` — Saldos sin modificar tras falla de red
16. `prop_caida_nodo_terminal.png` — Ejecución de caída de nodo
17. `prop_recuperacion_terminal.png` — Ejecución de recuperación
18. `prop_recuperacion_saldos.png` — Saldos consistentes tras recuperación

## Generar Informe PDF

```bash
cd informe
pdflatex main.tex
pdflatex main.tex
```

## Notas Técnicas

- El protocolo 2PC implementado es una versión simplificada para fines educativos.
- En producción, se recomienda usar extensiones de PostgreSQL como `pglogical` o `BDR` para replicación, o coordinadores externos como `Narayana`, `Atomikos`, o `Seata`.
- Las simulaciones de fallo usan excepciones Python; en un entorno real, los fallos de red se detectan mediante timeouts y heartbeats.

## Referencias

- Tanenbaum, A.S. (2008). _Sistemas distribuidos: principios y paradigmas_. Pearson Educación.
- PostgreSQL Documentation: https://www.postgresql.org/docs/
- Docker Documentation: https://docs.docker.com/
