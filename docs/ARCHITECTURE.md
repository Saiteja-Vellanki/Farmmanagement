# ARCHITECTURE.md — Farm Management / Farm Management App

## Screens identified from reference images
1. **My Farms (Home)** — farm list, cards with photo/area/crop/weather snippet, bottom nav (Home/Farms/Tasks/Reports/More).
2. **Add Farm — 5 step wizard**: Basic → Location → Crops → Farm Info → Review & Save. Stepper header on every step, `Next` / `Save Farm` at bottom.
3. **Farm Dashboard** — header (hamburger, farm name + dropdown, notification bell w/ badge), farm hero card (photo, location, weather, crop chips), 3×3 module grid (Workers, Fertilizer, Spraying, Irrigation, Machinery, Purchase, Harvest, Store, Expenses), Upcoming Work list, floating `+ Add Entry`, bottom nav (Home/Reports).
4. **Workers Daily Entry** — date picker, Total Workers / Total Labour Cost summary chips, multi-select "Who worked today" (Female/Male/Contract), conditional sections per type with stepper + wage + auto-computed total + payment mode/paid/pending, Contract Work section with status + balance + repeatable payments, notes, sticky bottom summary bar + Save Entry.

## Navigation graph
```
MyFarmsScreen (start)
 ├─ AddFarmWizard (5 steps, own back stack)
 ├─ FarmDashboard/{farmId}
 │   ├─ WorkersDailyEntry/{farmId}/{date?}
 │   ├─ WorkerHistory/{farmId}
 │   ├─ Fertilizer / Spraying / Irrigation / Machinery / Purchase / Harvest / Store / Expenses (list+detail+add, per module)
 │   ├─ UpcomingWorkAll/{farmId}
 │   └─ Reports/{farmId}
 ├─ Tasks
 ├─ Reports (global)
 └─ Settings → BackupRestore
```
Single-Activity, Jetpack Navigation Compose. Each top-level module = its own NavGraph (feature-module style within one Gradle module for now; can be split into Gradle modules later if build time becomes an issue).

## Architecture pattern
MVVM + Repository, unidirectional data flow:
`Compose UI → ViewModel (StateFlow<UiState>) → Repository → Room DAO (Flow) → SQLite`

- **Presentation**: Jetpack Compose, Material 3, one `ViewModel` per screen, `sealed class UiState`.
- **Domain-ish layer**: kept thin — Repositories contain calculation logic (totals, pending amounts, dashboard aggregates) rather than a separate use-case layer, to match team size/velocity. Can be promoted to use-cases later if logic grows.
- **Data**: Room (single `AppDatabase`), `Flow`-returning DAOs so dashboard numbers update reactively when any module writes data.
- **DI**: manual DI via a simple `AppContainer` (no Hilt yet, to keep build light — can migrate to Hilt in a later phase without UI changes).

## Multi-farm model
Every transactional entity (WorkerDailyEntry, FertilizerApplication, Purchase, Expense, etc.) carries a non-null `farmId` foreign key. All dashboard queries and reports are scoped `WHERE farmId = :currentFarmId`. Current farm selection is held in a `DataStore<Preferences>` (`selected_farm_id`) so it survives process death, and exposed as `StateFlow<Long>` from a shared `FarmSelectionRepository`.

## Modules (feature packages)
`farm`, `worker`, `fertilizer`, `spraying`, `irrigation`, `machinery`, `purchase`, `harvest`, `inventory`, `expense`, `task`, `report`, `weather`, `backup`, `ai` (optional) — each with `entity/`, `dao/`, `repository/`, `ui/`.

## Offline-first contract
Everything except: map tiles, reverse-geocoding, weather refresh, optional cloud AI photo analysis. Those features degrade gracefully (cached weather + timestamp, GPS-only lat/lng without map tiles, disabled AI button with explanation) when offline.

## Build/tooling
Kotlin, Gradle (Kotlin DSL), Jetpack Compose + Material 3, Room (KSP), Navigation-Compose, DataStore, WorkManager (reminders/notifications), CameraX + Photo Picker, Coil (image loading), GitHub Actions for debug/release builds.
