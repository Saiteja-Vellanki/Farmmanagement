# DEVELOPMENT_PLAN.md

Phases follow the spec's 10-phase breakdown. Status reflects this session's output.

| Phase | Scope | Status |
|---|---|---|
| 1 | Project setup, theme, navigation skeleton, Farm dashboard UI, Add Farm 5-step wizard UI | **Scaffolded this session** (see below) |
| 2 | Room DB, Farm/Crop/Supervisor entities+DAOs, search/filter | Entities from `DATABASE_SCHEMA.md` started; DAOs pending |
| 3 | Workers module: daily entry, wages, payments, contract work, history | UI scaffold for Daily Entry started; persistence pending |
| 4 | Fertilizer, Spraying, Irrigation, Machinery | Not started |
| 5 | Purchase, Harvest, Store/Inventory, Expenses | Not started |
| 6 | Tasks, Notifications, Reports, live dashboard calculations | Dashboard UI wired to placeholder data; needs Flow-based queries |
| 7 | GPS, Maps, boundary, Camera, Weather | Not started |
| 8 | Backup/Restore, optional AI | Not started |
| 9 | Security hardening, testing, performance | Not started |
| 10 | GitHub Actions, release AAB, Play Store prep | Doc + workflow templates provided; not wired to a real signed build yet |

## What's in this session's deliverable
- 9 planning docs (this folder)
- Gradle project skeleton (Kotlin DSL, Compose, Material3, Room+KSP, Navigation-Compose, DataStore)
- Room entities: `Farm`, `Crop`, `Supervisor` (rest of schema documented, not yet coded)
- Compose UI: `FarmDashboardScreen` (matches image 3, reading from a ViewModel — currently backed by an in-memory fake repo so the screen is visually complete before Room wiring), `AddFarmWizard` scaffold (5-step, Basic step fully built, others as structured stubs), theme (green Material3 palette), navigation graph connecting Dashboard ↔ Add Farm.
- `.github/workflows/debug-build.yml` and `release-build.yml`

## What's intentionally not done yet (and why)
Full Room persistence, GPS/maps, camera, weather, backup/restore, and the other 8 dashboard modules are substantial features each — building all of them un-tested in one pass would violate the spec's own rule ("do not create a fake prototype... build in phases, verify each phase"). Recommended next step: continue phase-by-phase, either in follow-up messages here or, given the size of this project, using **Claude Code** against a real GitHub repo so each phase can actually be compiled, tested on a device/emulator, and committed — this sandbox has no Android SDK/emulator, so nothing here has been build-verified.

## Immediate next actions
1. You create (or point me to) the GitHub repo; push this scaffold.
2. Open in Android Studio, sync Gradle, confirm Phase 1 UI renders/navigates correctly on a device/emulator.
3. Resume at Phase 2: wire real Room DAOs behind `FarmDashboardViewModel` and `AddFarmViewModel`, replacing the fake in-memory repo.
