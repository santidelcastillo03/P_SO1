# Plan de Proyecto Ágil (Versión Completa): Simulador de Planificación de SO

> **Alineado 1:1 con el Canvas de Requisitos (RF/RNF).** Incluye épicas, *user stories*, criterios de aceptación, trazabilidad y roadmap sugerido.

**Proyecto:** Simulación de Planificación de Procesos  
**Fecha:** 2025-09-28

---

## 1) Épicas del Proyecto
- **Epic 1:** Gestión del Ciclo de Vida de los Procesos  
- **Epic 2:** Planificación de CPU y Despacho  
- **Epic 3:** Interfaz Gráfica y Visualización en Tiempo Real  
- **Epic 4:** Configuración, Persistencia y Métricas  

> **Trazabilidad global:**  
> - Epic 1 cubre RF-01.* y parcialmente RF-02.5, RF-02.7.  
> - Epic 2 cubre RF-02.* (reloj, políticas, CPU, E/S).  
> - Epic 3 cubre RF-03.* (GUI/visualización).  
> - Epic 4 cubre RF-04.* (métricas, gráfica, persistencia, log).  
> - Todas las épicas respetan RNF-01…RNF-11.

---

## 2) Desglose Detallado de User Stories

### Epic 1: Gestión del Ciclo de Vida de los Procesos

#### US 1.1 — Estructura Base del PCB
**Descripción:** Como **SISTEMA**, necesito una clase para representar el PCB con sus identificadores básicos, para que cada proceso sea una entidad única y rastreable.  
**Tareas Técnicas**
- Crear `ProcessState.java` con: `NUEVO`, `LISTO`, `EJECUCION`, `BLOQUEADO`, `TERMINADO`, `LISTO_SUSPENDIDO`, `BLOQUEADO_SUSPENDIDO`.
- Crear `ProcessControlBlock.java` con: `processId:int`, `processName:String`, `processState:ProcessState`.
**Criterios de Aceptación**
- Se instancian PCBs válidos con estados del enum; ids únicos y dinámicos.
- Validaciones básicas de nulos y rangos; `toString()` legible.  
**Trazabilidad:** RF-01.1, RF-01.3, RF-01.6; RNF-01, RNF-02.

---

#### US 1.2 — Contexto y Planificación en el PCB
**Descripción:** Como **SISTEMA**, el PCB debe almacenar contexto de hardware y datos para el planificador.  
**Tareas Técnicas**
- Añadir en `ProcessControlBlock`: `programCounter:int`, `memoryAddressRegister:int`.
- Planificación: `totalInstructions:int`, `isIOBound:boolean`, `ioExceptionCycle:int`, `ioDuration:int`.
- Métricas: `creationTime:long`, `completionTime:long`.
**Criterios de Aceptación**
- Se persiste y lee el estado completo sin pérdida; getters/setters cubiertos por pruebas.  
**Trazabilidad:** RF-01.2, RF-01.6; RF-02.6; RNF-01, RNF-02.

---

#### US 1.3 — Implementar Cola Propia (sin librerías)
**Descripción:** Como **DESARROLLADOR**, necesito una cola genérica desde cero para cumplir la restricción de DS.  
**Tareas Técnicas**
- `Node<T>.java` (lista enlazada).
- `CustomQueue<T>.java`: `enqueue`, `dequeue`, `peek`, `isEmpty`, `size` (O(1) amortizado).
**Criterios de Aceptación**
- FIFO garantizado; pruebas de límites (cola vacía) y concurrencia básica.  
**Trazabilidad:** RF-02.7; RNF-03, RNF-05.

---

#### US 1.4 — Integrar Colas de Estado en el Simulador
**Descripción:** Como **SO**, gestionar colas por estado.  
**Tareas Técnicas**
- En `OperatingSystem`: `readyQueue`, `blockedQueue`, `finishedProcessesList` (`CustomQueue<PCB>`).
- Transiciones atómicas: `moveToReady(PCB)`, `moveToBlocked(PCB)`, `markAsFinished(PCB)` (sincronizar si procede).
**Criterios de Aceptación**
- Transiciones correctas sin condiciones de carrera; logs de auditoría visibles.  
**Trazabilidad:** RF-01.3, RF-01.4, RF-01.5, RF-02.7; RNF-05.

---

#### US 1.5 — Transición a BLOQUEADO por E/S
**Descripción:** Como **PROCESO** en ejecución, debo pasar a **BLOQUEADO** cuando ocurra la E/S.  
**Tareas Técnicas**
- En ciclo de CPU: si `programCounter == ioExceptionCycle`, `OperatingSystem.moveToBlocked(currentProcess)` y encolar en `IOHandler`.
**Criterios de Aceptación**
- El proceso pasa a `BLOQUEADO` en el ciclo esperado; CPU queda libre.  
**Trazabilidad:** RF-01.2, RF-02.5, RF-02.6.

---

#### US 1.6 — Manejador de E/S Asíncrono
**Descripción:** Como **MANEJADOR E/S**, procesar en hilo separado para simular asincronía.  
**Tareas Técnicas**
- `IOHandler implements Runnable`.
- Acceso seguro a `blockedQueue` (Semaphore/Lock).
- `sleep(pcb.ioDuration * cycleDuration)`; reinyectar en `readyQueue`.
**Criterios de Aceptación**
- Sin deadlocks; flujo continuo de retorno a `readyQueue`.  
**Trazabilidad:** RF-02.5; RNF-05.

---

#### US 1.7 — Gestionar Estado **Suspendido**
**Descripción:** Como **SO**, mover procesos a **Suspendido** cuando memoria esté llena y retornarlos luego.  
**Tareas Técnicas**
- Parámetro `MAX_PROCESSES_IN_MEMORY`.
- Colas: `readySuspendedQueue`, `blockedSuspendedQueue`.
- Si se supera límite al cargar, mover un candidato (p. ej., menor prioridad/antigüedad) a cola de suspendidos.
- Al liberar memoria (proceso termina), retornar uno desde suspendidos a `ready`.
**Criterios de Aceptación**
- Invariante de capacidad se cumple; no hay pérdida de procesos.  
**Trazabilidad:** RF-01.4, RF-01.5; RNF-05.

---

### Epic 2: Planificación de CPU y Despacho

#### US 2.1 — Reloj del Sistema
**Descripción:** Como **SISTEMA**, necesito un hilo que simule ciclos de reloj.  
**Tareas Técnicas**
- En `OperatingSystem`, crear `Thread` principal; `globalClockCycle++` por iteración.
- Orden: planificador → despachador → `CPU.executeCycle()`; `sleep(cycleDuration)`.
**Criterios de Aceptación**
- Reloj estable, configurable y observable desde la UI/log.  
**Trazabilidad:** RF-02.1, RF-02.2; RNF-05.

---

#### US 2.2 — Interfaz de Planificador (Strategy)
**Descripción:** Como **DESARROLLADOR**, quiero una interfaz común para políticas.  
**Tareas Técnicas**
- `SchedulerPolicy.java`: `ProcessControlBlock selectNextProcess(CustomQueue<ProcessControlBlock> readyQueue)`.
- En `OperatingSystem`: `private SchedulerPolicy currentPolicy;` + `setPolicy(...)`.
**Criterios de Aceptación**
- Cambio de política en runtime sin recompilar; pruebas con mocks.  
**Trazabilidad:** RF-02.3, RF-02.4.

---

#### US 2.3–2.8 — Las 6 Políticas de Planificación
**Descripción:** Como **PLANIFICADOR**, implementar FCFS, SJF, Round Robin y las restantes del libro de **Stallings**.  
**Tareas Técnicas**
- Clase por política implementando `SchedulerPolicy`.
- **SJF**: iterar `readyQueue` para menor `totalInstructions` (requiere `remove(pcb)` en `CustomQueue`).  
- **RR**: añadir `quantumRemaining:int` en PCB; decrementar por ciclo; si 0 y no terminó, reencolar y resetear.
- Implementar las otras políticas conforme a Stallings (p. ej., **Prioridad**, **SRTF**, **MLFQ** si aplica a la consigna local).
**Criterios de Aceptación**
- Selección correcta por política; pruebas sintéticas que evidencien comportamiento esperado.  
**Trazabilidad:** RF-02.3, RF-02.4, RF-02.7.

> **Nota:** La consigna exige 6 políticas; si el alcance del curso fija una lista exacta, reflejarla aquí y en la UI (RF-03.5).

---

#### US 2.9 — Despachador y Simulación de CPU
**Descripción:** Como **CPU**, recibir proceso y simular ejecución por instrucción.  
**Tareas Técnicas**
- `CPU.java` con `private ProcessControlBlock currentProcess;`
- `cpu.loadProcess(pcb)` tras selección del planificador.
- `cpu.executeCycle()`: incrementar PC y MAR; verificar finalización/bloqueo.
**Criterios de Aceptación**
- PC/MAR avanzan de manera lineal (RF-02.6); eventos de fin/bloqueo/log correctos.  
**Trazabilidad:** RF-02.6; RF-04.4.

---

### Epic 3: Interfaz Gráfica y Visualización en Tiempo Real

#### US 3.1 — Ventana Principal y Layout
**Descripción:** Como **USUARIO**, quiero una ventana con secciones claras.  
**Tareas Técnicas**
- `MainFrame` (JFrame); paneles: `queuesPanel`, `cpuPanel`, `controlsPanel`, `chartPanel` (BorderLayout/GridBagLayout).
**Criterios de Aceptación**
- Ventana redimensionable; áreas diferenciadas.  
**Trazabilidad:** RF-03.1.

---

#### US 3.2 — Visualización Dinámica de Colas
**Descripción:** Como **USUARIO**, ver colas de listos, bloqueados y terminados en tiempo real.  
**Tareas Técnicas**
- `JList + DefaultListModel` por cola; para terminados: `JTextArea` o `JList`.
- `updateQueueViews()` para refrescar; llamar vía `SwingUtilities.invokeLater()`.
**Criterios de Aceptación**
- Cambios de orden visibles de inmediato (RF-03.2).  
**Trazabilidad:** RF-03.1, RF-03.2, RF-03.4.

---

#### US 3.3 — Panel de Estado del CPU
**Descripción:** Como **USUARIO**, ver proceso en CPU, PC, ciclo actual y **modo** (OS/Usuario).  
**Tareas Técnicas**
- `JLabels` en `cpuPanel` + `JLabel modo` = "OS"/"Usuario".
- `updateCpuView(pcb, clockCycle, modo)` vía `invokeLater`.
**Criterios de Aceptación**
- Información exacta por ciclo; modo reflejado correctamente.  
**Trazabilidad:** RF-03.3, RF-03.6.

---

#### US 3.4 — Controles de Simulación
**Descripción:** Como **USUARIO**, cambiar algoritmo y velocidad en runtime.  
**Tareas Técnicas**
- `JComboBox` (algoritmos) → `operatingSystem.setPolicy()`.
- `JSlider` (velocidad) → actualiza `cycleDuration`.
**Criterios de Aceptación**
- Cambios aplican sin reinicio; UI fluida (RNF-07).  
**Trazabilidad:** RF-02.2, RF-02.4, RF-03.5; RNF-07, RNF-08.

---

### Epic 4: Configuración, Persistencia y Métricas

#### US 4.1 — Persistencia de Procesos
**Descripción:** Como **USUARIO**, guardar/cargar procesos en JSON/CSV.  
**Tareas Técnicas**
- `ConfigurationManager.save/load` usando **Gson**; `ProcessData` (POJO).
- Botones **Guardar/Cargar** con `JFileChooser`.
**Criterios de Aceptación**
- Datos válidos se guardan/cargan; manejo básico de errores/formatos.  
**Trazabilidad:** RF-04.3; RNF-04.

---

#### US 4.2 — Métricas de Rendimiento
**Descripción:** Como **SISTEMA**, calcular throughput, utilización, equidad y tiempo de respuesta.  
**Tareas Técnicas**
- `MetricsCalculator`: notificación de ocupación de CPU por ciclo y de procesos terminados (creation/completion).
- Métodos: `getThroughput()`, `getCpuUtilization()`, `getAvgResponseTime()` y **Equidad** (p. ej., Índice de Jain: \(J = (\sum x_i)^2 / (n \cdot \sum x_i^2)\) sobre tiempos de espera por proceso).
**Criterios de Aceptación**
- Resultados consistentes con definiciones; pruebas con escenarios sintéticos.  
**Trazabilidad:** RF-04.1.

---

#### US 4.3 — Gráficas en Tiempo Real
**Descripción:** Como **USUARIO**, ver evolución del rendimiento.  
**Tareas Técnicas**
- Integrar **JFreeChart** (`MetricsChartPanel`); `XYSeries` por métrica.
- Cada *N* ciclos, muestrear `MetricsCalculator` y añadir puntos.
**Criterios de Aceptación**
- Gráfico fluido sin congelar UI (RNF-07).  
**Trazabilidad:** RF-04.2; RNF-04, RNF-07.

---

#### US 4.4 — Log de Decisiones del Planificador
**Descripción:** Como **USUARIO**, quiero un log de texto para auditar decisiones.  
**Tareas Técnicas**
- `Logger.log(String message)` → `simulation.log` (y opcional a un `JTextArea` en GUI).
- Loggear: selección de proceso, cambios de estado, fin de E/S, cambio de política, fin de quantum.
**Criterios de Aceptación**
- Entradas claras con timestamp y `globalClockCycle`.  
**Trazabilidad:** RF-04.4.

---

## 3) Reglas Transversales (DoD / Calidad)
- **RNF-01** Java > 21; **RNF-02** NetBeans; **RNF-03** sin colecciones de Java para DS; **RNF-04** solo Gson/JFreeChart.  
- **RNF-05** Concurrencia: Threads obligatorios; Semáforos/Locks donde exista acceso compartido.  
- **RNF-06** GitHub obligatorio (branching simple: `main` + `dev` + PRs).  
- **RNF-07** UI fluida; actualizar solo con `SwingUtilities.invokeLater`.  
- **RNF-08** UI explicativa (labels, indicadores de política y modo).  
- **RNF-09/10/11** Informe PDF + repo GitHub; ejecución correcta en demo.

**Pruebas mínimas**
- Unitarias: `CustomQueue`, `FCFSPolicy`, `SJFPolicy`, `RoundRobinPolicy`, `MetricsCalculator`.  
- Integración: flujo OS-CPU-IO con 3–5 procesos representativos (CPU-bound/I/O-bound).

---

## 4) Roadmap Sugerido 

- **Sprint 1:** US 1.1, 1.2, 1.3 (PCB + Contexto + Cola)  
  *Entrega:* pruebas unitarias de `CustomQueue` y PCB.
- **Sprint 2:** US 1.4, 2.1 (colas en SO + reloj)  
  *Entrega:* consola mostrando `globalClockCycle` y estados.
- **Sprint 3:** US 2.2, 2.3, 2.9 (Strategy + FCFS + CPU)  
  *Entrega:* FCFS funcional con demo mínima.
- **Sprint 4:** US 2.4, 2.5, 1.5, 1.6 (SJF + RR + E/S asincrónica)  
  *Entrega:* casos mixtos CPU/I-O; quantum ajustable.
- **Sprint 5:** US 3.1–3.4 (GUI base + controles + modo)  
  *Entrega:* UI fluida con colas/CPU/mode selector.
- **Sprint 6:** US 4.1–4.4 (persistencia + métricas + gráfico + log)  
  *Entrega:* gráfico en tiempo real + `simulation.log`.

---

## 5) Gestión de Riesgos y Mitigación
- **Concurrencia/Deadlocks:** usar Semáforos/Locks, minimizar regiones críticas, pruebas de estrés.  
- **Rendimiento UI:** muestrear métricas cada *N* ciclos; usar `invokeLater`; evitar trabajo pesado en EDT.  
- **Precisión temporal:** desacoplar `cycleDuration` de tiempos de I/O; parametrizar.  
- **Complejidad de políticas:** empezar por FCFS/RR/SJF y luego extender a 6 políticas con el mismo patrón.

---

## 6) Matriz de Trazabilidad (resumen)
- **RF-01.1/01.3/01.6 →** US 1.1, 1.2, 1.4  
- **RF-01.2/02.5 →** US 1.2, 1.5, 1.6  
- **RF-01.4/01.5 →** US 1.7  
- **RF-02.1/02.2 →** US 2.1, 3.4  
- **RF-02.3/02.4/02.7 →** US 2.2, 2.3–2.8  
- **RF-02.6 →** US 2.9  
- **RF-03.* →** US 3.1–3.4  
- **RF-04.* →** US 4.1–4.4  
- **RNF-01…11 →** DoD y criterios por US/UI.

---

## 7) Definiciones de Métricas (referencia rápida)
- **Throughput:** procesos completados / unidad de tiempo (ciclos o segundos).  
- **Utilización de CPU:** ciclos con CPU ocupada / ciclos totales.  
- **Tiempo de respuesta promedio:** promedio de (primer despacho − creación).  
- **Equidad (Jain):** \( J = (\sum x_i)^2 / (n \cdot \sum x_i^2) \) sobre tiempos de espera o de respuesta (definir consistentemente).

---

### Checklist previa a entrega
- [ ] Java > 21; NetBeans; repo GitHub público/privado con acceso al profe.  
- [ ] 6 políticas implementadas y seleccionables en GUI.  
- [ ] `simulation.log` con eventos clave y `globalClockCycle`.  
- [ ] Persistencia JSON/CSV funcionando con validación básica.  
- [ ] Gráfico en tiempo real con XYSeries y muestreo cada *N* ciclos.  
- [ ] Informe PDF con diseño, decisiones y pruebas.

