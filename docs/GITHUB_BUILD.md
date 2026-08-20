# GITHUB_BUILD.md

## Repo hygiene
`.gitignore` excludes: `*.jks`, `*.keystore`, `keystore.properties`, `local.properties`, `/build`, `/.gradle`, `/captures`, `google-services.json` (if ever added).

## Secrets (GitHub → Settings → Secrets and variables → Actions)
- `RELEASE_KEYSTORE_BASE64` — base64 of the `.jks`/`.keystore` file
- `RELEASE_KEYSTORE_PASSWORD`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEY_PASSWORD`
- (optional) `WEATHER_API_KEY`, `MAPS_API_KEY` if not restricted client-side by package name/SHA-1 instead

## Workflows
### `.github/workflows/debug-build.yml`
Triggers on push/PR to any branch. Runs unit tests → assembles debug APK → uploads as build artifact.

### `.github/workflows/release-build.yml`
Triggers on tag `v*.*.*` (or manual `workflow_dispatch`). Steps:
1. Checkout, set up JDK 17, cache Gradle.
2. Run `./gradlew test` — fail fast if tests fail.
3. Decode `RELEASE_KEYSTORE_BASE64` to a temp keystore file.
4. `./gradlew assembleRelease bundleRelease` with signing config pulled from env vars mapped to the secrets above.
5. Upload both the signed APK and AAB as workflow artifacts (and optionally attach to a GitHub Release when triggered by a tag).

Both workflow YAML files are provided in `.github/workflows/` in this project (added once Phase 1 code is in the repo, since they reference actual module/gradle task names).
