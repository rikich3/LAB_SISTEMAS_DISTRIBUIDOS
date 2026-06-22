# Guía de Capturas para el Informe - Laboratorio 08

Guarda todas las capturas en: `informe/img/`

---

## EJERCICIOS RESUELTOS (FarmaAndes)

| Orden | Nombre del archivo                    | Momento exacto para capturar                       | Qué debe mostrar                                                                                                                       |
| ----- | ------------------------------------- | -------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| 1     | `docker_contenedores_up.png`          | Después de ejecutar `docker-compose up -d`         | Terminal o Docker Desktop mostrando los 4 contenedores corriendo (`almacen_arequipa`, `almacen_lima`, `banco_cusco`, `banco_trujillo`) |
| 2     | `resuelto_transferencia_terminal.png` | Ejecutar `python farmaandes_transferencia.py`      | Terminal con la salida completa: pasos 1-5 del 2PC, mensaje `[OK]`, y resultados finales (Arequipa 80, Lima 70)                        |
| 3     | `resuelto_transferencia_arequipa.png` | Inmediatamente después de la transferencia exitosa | pgAdmin/DBeaver: tabla `inventario` en `almacen_arequipa` con `stock = 80`                                                             |
| 4     | `resuelto_transferencia_lima.png`     | Inmediatamente después de la transferencia exitosa | pgAdmin/DBeaver: tabla `inventario` en `almacen_lima` con `stock = 70`                                                                 |
| 5     | `resuelto_fallo_terminal.png`         | Ejecutar `python farmaandes_fallo.py`              | Terminal mostrando `[ERROR] NODO LIMA NO RESPONDE`, `[ROLLBACK]`, y estado final (Arequipa 100, Lima 50)                               |
| 6     | `resuelto_fallo_arequipa.png`         | Después del rollback por fallo                     | pgAdmin/DBeaver: tabla `inventario` en `almacen_arequipa` con `stock = 100` (sin cambios)                                              |
| 7     | `resuelto_fallo_lima.png`             | Después del rollback por fallo                     | pgAdmin/DBeaver: tabla `inventario` en `almacen_lima` con `stock = 50` (sin cambios)                                                   |

---

## EJERCICIOS PROPUESTOS (Banco Cooperativo)

| Orden | Nombre del archivo                  | Momento exacto para capturar                         | Qué debe mostrar                                                                              |
| ----- | ----------------------------------- | ---------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| 8     | `prop_arquitectura.png`             | Antes de empezar, usa draw.io/PlantUML               | Diagrama de arquitectura con 3 nodos (Arequipa=Coordinador, Cusco/Trujillo=Participantes)     |
| 9     | `prop_diagrama_secuencia_ok.png`    | Antes de empezar, usa draw.io/PlantUML               | Diagrama de secuencia 2PC: Fase 1 (PREPARE+YES) → Fase 2 (COMMIT)                             |
| 10    | `prop_diagrama_secuencia_fallo.png` | Antes de empezar, usa draw.io/PlantUML               | Diagrama de secuencia 2PC con fallo: Fase 1 (PREPARE) → nodo caído → ROLLBACK                 |
| 11    | `prop_transferencia_terminal.png`   | Ejecutar `python banco_cooperativo.py transferencia` | Terminal con log 2PC completo: FASE 1 PREPARE, votos YES, FASE 2 COMMIT, y saldos finales     |
| 12    | `prop_saldo_arequipa_dbeaver.png`   | Inmediatamente después de la transferencia 2PC       | DBeaver/pgAdmin: tabla `cuentas` en `banco_arequipa` con `saldo` de `AQP-001` descontado      |
| 13    | `prop_saldo_cusco_dbeaver.png`      | Inmediatamente después de la transferencia 2PC       | DBeaver/pgAdmin: tabla `cuentas` en `banco_cusco` con `saldo` de `CUS-001` incrementado       |
| 14    | `prop_falla_red_terminal.png`       | Ejecutar `python banco_falla_red.py`                 | Terminal mostrando error de conexión con Cusco y `[ROLLBACK] Arequipa: transacción revertida` |
| 15    | `prop_falla_red_saldos.png`         | Después de la falla de red                           | DBeaver/pgAdmin: saldos de `AQP-001` y `CUS-001` sin cambios (verificar ambos nodos)          |
| 16    | `prop_caida_nodo_terminal.png`      | Ejecutar `python banco_caida_nodo.py`                | Terminal mostrando: `[Arequipa] COMMIT ejecutado` → `[Cusco] ERROR: Connection reset by peer` |
| 18    | `prop_recuperacion_terminal.png`    | Ejecutar `python banco_recuperacion.py`              | Terminal mostrando el protocolo de recuperación: `[Cusco] Reejecutando COMMIT`                |
| 19    | `prop_recuperacion_saldos.png`      | Después de la recuperación                           | DBeaver/pgAdmin: saldos consistentes en ambos nodos tras reejecutar COMMIT                    |

---

## Consejos prácticos para las capturas

1. **Terminal:** Maximiza la ventana y usa zoom (Ctrl++) para que el texto sea legible en el PDF.
2. **DBeaver/pgAdmin:** Captura solo la tabla/resultado, no toda la pantalla, para que se vea claro el valor.
3. **Diagramas:** Exporta desde draw.io en PNG con fondo blanco.
4. **Nombre exacto:** Respeta los guiones bajos (`_`) y extensiones `.png` tal como aparecen en la tabla.
5. **Ubicación:** Todas van dentro de `informe/img/`.

---

## Cómo insertar capturas en el informe LaTeX

Abre `informe/main.tex` y reemplaza cada bloque placeholder como este:

```latex
\fcolorbox{gray}{white}{\parbox{0.8\textwidth}{\centering\vspace{2cm}\textbf{CAPTURA:} ... \vspace{2cm}}}
```

Por el comando `\includegraphics` correspondiente:

```latex
\includegraphics[width=0.88\textwidth]{img/resuelto_transferencia_terminal.png}
```

Ejemplo para la primera captura:

```latex
\begin{figure}[H]
    \centering
    \includegraphics[width=0.88\textwidth]{img/docker_contenedores_up.png}
    \caption{Contenedores PostgreSQL en ejecución (Arequipa y Lima)}
\end{figure}
```

Para compilar el informe final:

```bash
cd informe
pdflatex main.tex
pdflatex main.tex
```
