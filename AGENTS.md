# Repository Guidelines

## Project Structure & Module Organization
Source lives under `src/` and is grouped by responsibility: `core/` holds OS simulation primitives, `datastructures/` wraps shared queue and node logic, `scheduler/` will host algorithms, `ui/` contains Swing panels and frames, and `util/` is reserved for helpers. NetBeans metadata sits in `nbproject/`; avoid hand-editing it unless syncing IDE settings. Assets such as manifests stay alongside build metadata (`manifest.mf`, `build.xml`). Keep new modules inside the matching Java package so Ant and NetBeans detect them automatically.

For clarity go to 'P_SO1/Plan_Agil_Completo_Simulador_SO.md' and 'P_SO1/Canvas_Requisitos_Simulador_SO.md'

## Build, Test, and Development Commands
```
ant clean       # remove compiled classes and dist artifacts
ant compile     # compile Java sources in src/
ant run         # launch the main class defined in nbproject/project.properties
ant test        # execute JUnit suites once tests exist under test/
```
Use NetBeans "Clean and Build" when working inside the IDE; it proxies these Ant targets. Create every file following how is done in the NetBeans IDE.

## Coding Style & Naming Conventions
Follow standard Java conventions: four-space indentation, no tabs, classes in PascalCase (`CustomQueue`), methods/fields in camelCase, and packages in lowercase (`scheduler`). Keep UI code thread-safe by wrapping Swing entry points in `SwingUtilities.invokeLater`, as shown in `src/ui/MainFrame.java`. Before committing, organize imports (`Shift+⌘+I` in NetBeans) and ensure no generated `TODO` stubs remain in production code. Comment on every function, how it works, what is for.

## Testing Guidelines
Add JUnit tests under a top-level `test/` folder mirroring package paths (`test/core/OperatingSystemTest.java`). Name test classes `*Test` and individual methods `shouldDescribeBehavior`. Aim to cover scheduler transitions and queue operations; flag timing-dependent logic with clear assertions rather than sleeps. Run `ant test` locally and include new tests whenever you introduce scheduling logic or data-structure changes.

## Commit & Pull Request Guidelines
Keep commits small and message them in the imperative mood (`Add RoundRobin scheduler`). Reference related artifacts with `[#task-id]` when applicable. Each pull request should include: purpose summary, affected modules, test evidence (`ant test` output or manual steps for UI changes), and screenshots for UI tweaks. Avoid committing IDE-generated build outputs; rely on `.gitignore` already present in history.

## Development Environment Tips
Use JDK 17+ to match current NetBeans defaults. When adding external libraries, place JARs in `lib/` and register them through NetBeans so `nbproject/project.properties` stays consistent. Document any simulator configuration (process counts, quantum) in the PR description to help reviewers reproduce scenarios.
