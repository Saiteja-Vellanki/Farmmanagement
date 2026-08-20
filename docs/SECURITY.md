# SECURITY.md

## Local data
- Room database and photos live in app-private storage (`context.filesDir` / `getDatabasePath`), not shared/external storage — not visible to other apps, not visible via USB file browsing.
- No plaintext secrets in source or Git history. Signing keystore + `keystore.properties` are `.gitignore`d; release signing values are injected via **GitHub Secrets** in CI, never committed.
- No verbose logging of financial/personal data (wages, phone numbers, payment references) in release builds — wrap debug logs with `if (BuildConfig.DEBUG)` and strip via ProGuard/R8 `-assumenosideeffects` rules for `Log.*` in release.

## Optional at-rest encryption
If the user opts into database encryption (Settings → Privacy), use **SQLCipher for Android** with the passphrase stored in the **Android Keystore** (hardware-backed where available) via `EncryptedSharedPreferences`/`Jetpack Security` — never store the passphrase in plaintext prefs or source. This is optional because it adds real complexity/perf cost; default is standard Room without SQLCipher, protected by normal app sandboxing.

## Permissions — minimum necessary
- `CAMERA` — only for farm/AI photos, requested at point of use.
- Fine/coarse `LOCATION` — only when adding/editing a farm or logging GPS; **no background location permission is requested**.
- `POST_NOTIFICATIONS` (API 33+) — for local reminders.
- No `READ/WRITE_EXTERNAL_STORAGE` on API 29+ (use scoped storage + Photo Picker); only `READ_MEDIA_IMAGES` fallback where required by minSdk.
- No contacts, SMS, call log, or background-location permissions of any kind.

## Backup/restore hardening
- Exported backup is a signed/checksummed archive (`BackupMetadata.checksum`, e.g. SHA-256) containing a JSON data dump + a photos folder.
- On import: validate checksum, validate JSON schema/version before touching the live DB, run import inside a Room transaction so a partial/corrupt file cannot leave the DB in a broken state, reject files that don't match the expected schema version, and never execute code embedded in a backup file (data-only format, no scripts/macros).
- User is explicitly asked **Merge or Replace** before import proceeds; default is the safer, non-destructive **Merge**.

## Network-facing surfaces
Only weather API, map/tile provider, reverse-geocoding, and (opt-in) cloud AI image analysis touch the network. Everything else — including all financial/labour data — never leaves the device. See PRIVACY_POLICY.md for exact data flows.

## Dependency hygiene
Pin dependency versions, review before upgrading, use Android Studio's built-in dependency vulnerability check / `./gradlew dependencyCheckAnalyze` if added, and keep target/minSdk aligned with current Play requirements (see PLAY_STORE_COMPLIANCE.md).
