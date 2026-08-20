# UI_FLOW.md

## Design language (from reference images)
- Primary: deep agricultural green (`#1B5E20`-ish header, `#2E7D32` accents), white cards, rounded 12–16dp corners, soft shadows.
- Category colors: Female = pink/red accent, Male = blue accent, Contract = orange accent, Purchase = purple, Store = amber, Expenses = red/wallet.
- Icons: simple filled circular-badge icons (people, leaf, spray bottle, droplet, tractor, cart, fruit, box, wallet).
- Large primary CTA buttons, full-width, rounded, dark green fill with white text + leading icon.
- Financial values always bold and color-matched to their section.

## Flow 1 — First run / Home
`My Farms (Home)` → tap `+ Add Farm` → 5-step wizard → `Save Farm` → returns to My Farms with new card → tap a farm card → `Farm Dashboard`.

## Flow 2 — Add Farm wizard
Step indicator (1–5) always visible and tappable-back only to completed steps.
1. Basic: photo (camera/gallery), name, type, area, auto Farm ID, Supervisor sub-card.
2. Location: Use Current Location / Select on Map, cascading State→District→Mandal→Village pickers, GPS lat/lng (editable), optional boundary polygon drawing.
3. Crops: repeatable crop card (crop, variety, area, plants, planting year, stage), `+ Add Another Crop`.
4. Farm Info: soil/irrigation/water/electricity/motor fields, remarks; supervisor fields repeated here in the 6-step variant (image 4) — reconciled by keeping Supervisor only on Step 1 and showing it read-only on Review.
5. Review & Save: collapsible summary cards per section, each with `Edit` jumping back to that step; `Save Farm` commits everything transactionally.

## Flow 3 — Dashboard → module
Dashboard grid tap → module list screen (e.g. Fertilizer history) → `+` FAB or bottom `+ Add Entry` → module's add/edit form → Save → returns to list, dashboard tile recalculates on next composition (Flow-driven, no manual refresh needed).

## Flow 4 — Workers Daily Entry (core flow, matches image 1 exactly)
1. Pick Work Date (defaults today).
2. "Who worked today?" — Female/Male/Contract are independent checkboxes; sections appear/disappear immediately below, no page nav.
3. Per active section: enter stepper count + wage → Total Amount auto-computes and displays in a highlighted mini-card `(count × wage)`.
4. Payment Mode chips (Cash/UPI/Account) → Paid Amount input → Pending Amount auto-computes (`total - paid`, clamped ≥ 0) and turns green at 0.
5. Contract section is structurally different: has its own amount/status/balance instead of count×wage, plus `+ Add Another Payment for this Contract` which appends a `ContractPayment` row without leaving the screen.
6. Free-text Work Details / Notes.
7. Sticky bottom bar recomputes Female/Male/Contract/Total Workers/Total Labour Cost live from current form state.
8. `SAVE ENTRY` — validates required fields (date, at least one section with count>0 or a named contract), writes via a single Room transaction, shows confirmation, navigates back to Dashboard (Workers tile refreshes automatically).

## Reusable components to build once
`SectionCard`, `AmountStepper`, `PaymentModeChips`, `AutoCalcAmountBox`, `StickyTotalsBar`, `DateFieldWithPicker`, `WizardStepIndicator`, `DashboardModuleTile`, `UpcomingWorkRow`.
