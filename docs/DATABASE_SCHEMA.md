# DATABASE_SCHEMA.md — Room / SQLite

All monetary columns are `Long` (paise/lowest unit) or `Double` rupees — this project uses `Double` for simplicity with 2-decimal rounding at display time. All entities have `id: Long` (autogenerate) PK unless noted. `farmId` is a foreign key to `Farm.id` with `onDelete = CASCADE` on every child table.

## Core
**Farm**(id, name, photoPath, farmType, totalArea, areaUnit, farmIdCode, state, district, mandal, village, latitude, longitude, boundaryGeoJson, soilType, irrigationType, waterSource, borewellCount, electricityConnection, motorHp, irrigationSystem, remarks, createdAt, updatedAt)

**Crop**(id, farmId→Farm, cropName, variety, area, areaUnit, plantCount, plantingYear, currentStage)

**FarmPhoto**(id, farmId→Farm, filePath, takenAt, caption)

**Supervisor**(id, farmId→Farm, name, phone, monthlySalary)

## Workers & Labour
**Worker**(id, farmId→Farm, name, gender[MALE/FEMALE], phone, workerCategory[DAILY/CONTRACT/PERMANENT], defaultWage, active)

**WorkerDailyEntry**(id, farmId→Farm, workDate, femaleCount, femaleWagePerPerson, femaleTotal, femalePaymentMode, femalePaid, femalePending, femaleNote, femaleTime, maleCount, maleWagePerPerson, maleTotal, malePaymentMode, malePaid, malePending, maleUpiRef, maleNote, maleTime, workDetails, totalWorkers, totalLabourCost)
  — one row per farm per date; female/male blocks flattened onto the entry per the reference UI (simplifies "18 Workers Today" style dashboard queries). `ContractWork` is a separate linked table since it's 1..N per date.

**WorkerAttendance**(id, farmId→Farm, workerId→Worker, dailyEntryId→WorkerDailyEntry, present, hoursWorked)
  — optional per-named-worker granularity if the user later wants named attendance instead of headcounts.

**WorkerPayment**(id, farmId→Farm, dailyEntryId→WorkerDailyEntry?, workerId→Worker?, amount, mode, paidAt, referenceNo, note)

**ContractWork**(id, farmId→Farm, dailyEntryId→WorkerDailyEntry, contractName, contractAmount, description, status[PLANNED/IN_PROGRESS/COMPLETED/CANCELLED], totalPaid, balanceAmount)

**ContractPayment**(id, contractWorkId→ContractWork, amount, mode, paymentDate, paymentTime, upiRef, note)

## Operations
**FertilizerApplication**(id, farmId→Farm, cropId→Crop?, block, fertilizerName, type, quantity, unit, applicationDate, method, cost, labourCost, notes, nextApplicationDate)

**SprayingApplication**(id, farmId→Farm, cropId→Crop?, block, productName, purpose, quantity, unit, applicationDate, method, labourCost, productCost, weatherNotes, nextSprayingDate, remarks)

**IrrigationEntry**(id, farmId→Farm, cropId→Crop?, block, waterSource, borewellName, motorName, startTime, endTime, durationMinutes, waterUsage, electricityCost, notes)

**Machinery**(id, farmId→Farm, name, type, registrationId, purchaseDate, purchaseCost, status)
**MachineryUsage**(id, machineryId→Machinery, usageDate, hours, fuelQuantity, fuelCost, notes)
**MachineryMaintenance**(id, machineryId→Machinery, serviceDate, nextServiceDate, cost, notes)

## Commerce
**Purchase**(id, farmId→Farm, date, supplier, category, item, quantity, unit, unitPrice, totalAmount, paymentMode, paymentStatus, notes)

**Harvest**(id, farmId→Farm, cropId→Crop?, block, harvestDate, quantity, unit, grade, quality, sellingPrice, buyer, totalSaleValue, labourCost, transportCost, notes)

**InventoryItem**(id, farmId→Farm, itemName, category, quantity, unit, purchasePrice, currentValue, supplier, purchaseDate, expiryDate, storageLocation, minStockLevel)
**InventoryTransaction**(id, inventoryItemId→InventoryItem, type[IN/OUT/ADJUST], quantity, date, note)

**Expense**(id, farmId→Farm, date, category, amount, cropId→Crop?, paymentMode, description, notes)

## Planning / system
**Task**(id, farmId→Farm, title, category, dueDate, status, priority, notes)
**AIAnalysis**(id, farmId→Farm, cropId→Crop?, photoPath, provider, resultJson, confidence, createdAt, wasCloudSent)
**WeatherCache**(id, farmId→Farm, fetchedAt, tempC, condition, rainChance, humidity, windKmh, rawJson)
**BackupMetadata**(id, createdAt, filePath, appVersion, entryCount, checksum)

## Indices
Every `farmId` column indexed. `WorkerDailyEntry(farmId, workDate)` unique-composite index (one entry per farm/date, matching the reference UI's single-entry-per-day model — "Add Another Payment" only applies within ContractPayment).

## Migrations
Room `fallbackToDestructiveMigration()` is **forbidden** post-v1 release. Every schema change ships an explicit `Migration` + a Room schema JSON export (`exportSchema = true`) checked into `app/schemas/`.
