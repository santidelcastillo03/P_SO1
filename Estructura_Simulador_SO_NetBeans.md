# Estructura de Directorios para el Simulador de SO en NetBeans

Esta es una estructura de paquetes recomendada para un proyecto **Ant** en **NetBeans**, diseñada para organizar lógicamente las clases del simulador según su funcionalidad.

## Vista del Proyecto en NetBeans

```
p_so1/
|
|-- src/
|   |
|   +-- core/
|   |   |-- CPU.java
|   |   |-- OperatingSystem.java
|   |   |-- ProcessControlBlock.java
|   |   |-- ProcessState.java
|   |
|   +-- datastructures/
|   |   |-- CustomQueue.java
|   |   |-- Node.java
|   |
|   +-- p_so1/
|   |   |-- P_so1.java
|   |
|   +-- scheduler/
|   |   |-- FCFS.java
|   |   |-- PolicyType.java
|   |   |-- PriorityNP.java
|   |   |-- PriorityP.java
|   |   |-- RoundRobin.java
|   |   |-- Scheduler.java
|   |   |-- SchedulingPolicy.java
|   |   |-- SJF.java
|   |   |-- SRTF.java
|   |
|   +-- ui/
|   |   |-- ControlsPanel.java
|   |   |-- CpuPanel.java
|   |   |-- LogPanel.java
|   |   |-- MainFrame.java
|   |   |-- MetricsPanel.java
|   |   |-- QueuesPanel.java
|   |
|   +-- util/
|       |-- ConfigManager.java
|       |-- IOHandler.java
|       |-- MetricsCalculator.java
|       |-- ProcessData.java
|
|-- nbproject/
|   |-- build-impl.xml
|   |-- genfiles.properties
|   |-- project.properties
|   |-- project.xml
|   +-- private/
|       |-- private.properties
|       |-- private.xml
|
|-- build.xml
|-- manifest.mf
|-- README.md
|-- Plan_Agil_Simulador_SO.md
|-- Estructura_Simulador_SO_NetBeans.md
```

## Explicación de la Estructura

- **`com.unimet.p_so`**: Paquete raíz. La convención *dominio invertido* (por ej. `com.tudominio.proyecto`) evita colisiones de nombres.
- **`core`**: Corazón del simulador. Contiene la lógica fundamental del SO (reloj, colas, CPU, PCB, estados).
- **`datastructures`**: Implementaciones propias de estructuras (p. ej., cola), ya que se prohíbe usar colecciones de Java.
- **`scheduling`**: Agrupa las políticas de planificación. Facilita añadir/alternar algoritmos.
- **`ui`**: Presentación con Swing. Mantiene separada la lógica de interfaz.
- **`util`**: Utilidades: E/S, persistencia (JSON/CSV), cálculo de métricas y tareas en hilos.
- **`Libraries (lib)`**: .jar externos necesarios (p. ej., **JFreeChart** para gráficos y **Gson** para JSON).

---

### Notas prácticas

1. **Dependencias**: coloca los `.jar` en `lib/` y añádelos al *Classpath* del proyecto en NetBeans.
2. **Separación de responsabilidades**: evita que la UI conozca detalles de `core` más allá de interfaces públicas.
3. **Extensibilidad**: nuevas políticas → implementan `SchedulerPolicy` y se registran sin tocar el resto del sistema.
4. **Pruebas**: considera un subpaquete `com.unimet.simso.tests` o un módulo de pruebas para validar `datastructures` y `scheduling`.
5. **Build**: `build.xml` de Ant debe copiar `lib/*.jar` al directorio de *dist* o incluirlos en el *manifest* para ejecución.
