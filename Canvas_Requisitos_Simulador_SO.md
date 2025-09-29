# Canvas de Requisitos: Simulador de Planificación de SO

**Proyecto:** Simulación de Planificación de Procesos  
**Fecha:** 2025-09-28  
**Basado en:** Documento *"Proyecto 1 Sistemas Operativos (2).pdf"*

---

## 1. Requisitos Funcionales (RF)


### RF-01: Gestión de Procesos y Estados

1. **RF-01.1: Creación de Procesos**
	- El usuario debe poder definir procesos especificando: **Nombre**, **Cantidad de Instrucciones**, y **tipo** (CPU-bound o I/O-bound).
2. **RF-01.2: Especificación de E/S**
	- Para procesos **I/O-bound**, el usuario debe definir: (1) **ciclos para generar** una excepción de E/S, y (2) **ciclos para satisfacerla**.
3. **RF-01.3: Modelo de 6 Estados**
	- El sistema debe implementar un modelo de estados que incluya: **Nuevo, Listo, Ejecución, Bloqueado, Terminado y Suspendido**.
4. **RF-01.4: Gestión de Suspensión**
	- El sistema debe identificar procesos que requieran más memoria y gestionar su transición a **Suspendido** (listos-suspendidos, bloqueados-suspendidos).
5. **RF-01.5: Retorno de Suspensión**
	- El sistema debe gestionar la transición de **Suspendido** de vuelta a **Listo** cuando se resuelva el bloqueo prolongado.
6. **RF-01.6: PCB por Proceso**
	- Cada proceso debe tener un **PCB** con, como mínimo: **ID** (único y dinámico), **Nombre**, **Estado** (Running, Blocked, Ready), **Program Counter (PC)** y **Memory Address Register (MAR)**.

---


### RF-02: Simulación y Planificación

1. **RF-02.1: Reloj de Simulación**
	- Debe existir un **reloj global** que avance en ciclos. El usuario debe poder **configurar la duración** de un ciclo (ms o s).
2. **RF-02.2: Modificación de Ciclo**
	- La **duración del ciclo** debe poder ser modificada **al inicio** y **en tiempo de ejecución**.
3. **RF-02.3: Políticas de Planificación**
	- Se deben implementar y poder seleccionar **6 políticas** de planificación especificadas en el libro de **Stallings**.
4. **RF-02.4: Intercambio de Políticas**
	- El usuario debe poder **intercambiar** el algoritmo de planificación **en tiempo de ejecución**.
5. **RF-02.5: Manejo de Excepciones E/S**
	- El tratamiento de excepciones debe realizarse con **Hilos (Threads)**. **Cada hilo de excepción debe regresar** al procesador donde fue generado.
6. **RF-02.6: Simplicidad de Ejecución**
	- Se asume que: (1) **todas las instrucciones** se ejecutan en **un único ciclo**; (2) la ejecución es **lineal** (PC y MAR se incrementan en 1 por ciclo).
7. **RF-02.7: Cola Única de Listos**
	- La asignación de procesos será **dinámica**, por lo que habrá **una única cola de listos** para el procesador.

---


### RF-03: Interfaz Gráfica (GUI) y Visualización

1. **RF-03.1: Visualización de Colas**
	- La GUI debe mostrar en **tiempo real** las colas de procesos **listos** y **bloqueados**, así como una **lista de procesos culminados**.
2. **RF-03.2: Visibilidad de Ordenamiento**
	- Cualquier **cambio en el orden** de las colas (p. ej., por prioridad, fin de quantum) debe ser **visible inmediatamente** en la GUI.
3. **RF-03.3: Estado del Procesador**
	- La GUI debe mostrar en todo momento: (1) el **proceso ejecutándose** en el CPU; (2) el **PC** y la **instrucción objetivo**; (3) el **número de ciclo** de reloj global.
4. **RF-03.4: Visualización de PCB**
	- Se debe poder ver la **información del PCB** de cada proceso en las colas y en el CPU.
5. **RF-03.5: Indicador de Planificación**
	- La GUI debe tener un **selector de algoritmo** y un **indicador** del algoritmo activo en tiempo real.
6. **RF-03.6: Indicador de Modo**
	- La GUI debe indicar claramente si se está ejecutando el **Sistema Operativo** o un **proceso de usuario**.

---


### RF-04: Métricas y Persistencia

1. **RF-04.1: Cálculo de Métricas**
	- El simulador debe calcular y registrar promedios de: **Throughput** (procesos/tiempo), **Utilización del Procesador**, **Equidad** y **Tiempo de Respuesta** (espera promedio).
2. **RF-04.2: Gráficos de Rendimiento**
	- Se debe mostrar en un **mismo gráfico** la **utilidad/métricas vs. tiempo** para visualizar el rendimiento del sistema.
3. **RF-04.3: Persistencia de Configuración**
	- La configuración de la **duración del ciclo** y parámetros de procesos debe poder **guardarse** en **CSV o JSON** y **cargarse** en futuras ejecuciones.
4. **RF-04.4: Log de Eventos**
	- La aplicación debe generar un **log de eventos** (texto) que registre decisiones del planificador (p. ej., *“Procesador selecciona Proceso C”*).

---


## 2. Requisitos No Funcionales (RNF)

1. **RNF-01: Tecnología**
	- El proyecto **DEBE** ser desarrollado en **Java**, específicamente en una versión **posterior a Java 21**.
2. **RNF-02: Entorno de Desarrollo**
	- El proyecto **DEBE** ser desarrollado usando el **IDE NetBeans**.
3. **RNF-03: Restricción de Librerías (Estructuras de Datos)**
	- **NO** se permite el uso de librerías de **estructuras de datos** (ArrayList, Queue, etc.). Deben ser implementadas por el equipo.
4. **RNF-04: Restricción de Librerías (Permitidas)**
	- **SÓLO** se permite uso de librerías externas para: (1) **gráfica de rendimiento**, (2) **leer/escribir** configuración (CSV/JSON).
5. **RNF-05: Concurrencia**
	- Uso **obligatorio de Threads** para simulación de procesos y **Semáforos** para exclusión mutua.
6. **RNF-06: Control de Versiones**
	- Uso **obligatorio** de un **repositorio GitHub** para control de versiones.
7. **RNF-07: Rendimiento Visual**
	- La **GUI** debe ser **fluida** y actualizarse en tiempo real sin demoras perceptibles.
8. **RNF-08: Usabilidad**
	- La interfaz debe ser **clara y detallada**, mostrando **cómo** el planificador selecciona procesos y reorganiza colas.
9. **RNF-09: Documentación**
	- Entregar un **informe en .PDF** con el código, describiendo clases/métodos clave y conclusiones de configuraciones.
10. **RNF-10: Entrega**
	 - Entrega final: **informe .PDF** + **link GitHub**, enviados por **correo** antes de fecha/hora estipuladas.
11. **RNF-11: Robustez (Criterios de Calificación)**
	 - El programa debe **ejecutarse adecuadamente**. Si no se ejecuta, **no tiene GUI**, o **no usa GitHub**, se califica con **cero**.

---

### Anexos (Opcionales para el Canvas)
- **Trazabilidad:** Los RF-02.* se relacionan con las *User Stories* del plan ágil (Reloj, Políticas, CPU, E/S).  
- **Suposiciones:** Instrucciones unitarias por ciclo; ejecución lineal (PC/MAR +1).  
- **Dependencias:** Java > 21, NetBeans, Gson/JFreeChart (permitidas).  
- **Riesgos:** Concurrencia y bloqueos; rendimiento de UI; precisión temporal de la simulación.  
- **Mitigación:** Semáforos/bloqueos finos, actualización de UI con `invokeLater`, pruebas por componentes.
