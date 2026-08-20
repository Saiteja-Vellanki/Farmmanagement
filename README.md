# Farm Management App — Phase 1

Native Kotlin/Compose, offline-first, Room-backed. **Phase 1 scope only**: three screens —
Welcome / My Farms → Add New Farm → Individual Farm Dashboard. No Workers, Fertilizer,
Spraying, Irrigation, Machinery, Purchase, Harvest, Store, or Expenses functionality yet —
those appear only as placeholder cards on the dashboard ("Coming in next phase").

## What's implemented
- **Screen 1 — Welcome / My Farms**: empty state, farm cards (photo, address, extent, live
  crop/motor counts from Room), `+ Add Farm`, bottom nav (Home | Reports placeholder).
- **Screen 2 — Add New Farm**: single scrollable page (no wizard steps), Farm Name, Farm
  Address + "Use Current Location" (GPS via `LocationManager`, one-shot, no background
  tracking), Total Extent + unit dropdown, repeatable Crop rows (free-text plant name + count,
  delete icon), repeatable Motor rows (Motor Type + HP dropdowns, delete icon), Farm Photo
  (Camera via `TakePicture` + FileProvider, or Gallery via Android Photo Picker, both copied
  into app-private storage), Supervisor section with a "No Supervisor" toggle, validation,
  large green **SAVE FARM** button.
- **Screen 3 — Individual Farm Dashboard**: loads by `farmId` (not farm name — see
  `data/db/entity/Farm.kt`), header with photo/name/address/crops, a weather placeholder
  (no fake values), and 9 management placeholder cards that show a "Coming in next phase"
  snackbar on tap.

## Data model
Room, 4 entities: `Farm`, `Crop`, `Motor`, `Supervisor`. Every child table is keyed by
`Farm.farmId` (a generated UUID string) — never by the farm name, and never by Room's
internal autoincrement `id`. See `data/repository/FarmRepository.kt` for the save flow.

## Local-only
No network permission is requested in Phase 1 — everything (including GPS reads) works
fully offline, as required.

## Build status
Iteratively fixed against real GitHub Actions CI output — Gradle wrapper, launcher icon
resources, and two Kotlin compile errors (missing import, missing `@OptIn` for experimental
Material3 `TopAppBar`) have all been resolved against actual build logs. Re-run CI after
pulling this in; if anything new surfaces, it should be much smaller than the earlier rounds.

## Testing on your device
1. Push this to your repo (or open locally in Android Studio).
2. Run on a device/emulator (API 26+).
3. Create Farm A with a couple of crops/motors, save, confirm it appears on Home with correct
   counts. Create Farm B with different crops. Open each — confirm no cross-contamination
   (see Phase 1 spec's isolation tests).
4. Try Add Farm with camera denied / GPS denied — confirm no crash, just a snackbar.

## Next (Phase 2+)
Real functionality behind the 9 dashboard placeholder cards, weather, maps, reports, backup/restore.
