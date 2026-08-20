# PLAY_STORE_COMPLIANCE.md

**This checklist must be re-verified against the live Google Play Console / Play policy pages at release time — requirements (target API level, policy wording) change over time and are not hardcoded here.**

## Pre-submission checklist
- [ ] `targetSdk` set to the currently-required Play level (verify at https://developer.android.com/google/play/requirements/target-sdk before release)
- [ ] `minSdk` chosen deliberately (recommend 26+ for scoped storage/Keystore APIs used here) and documented
- [ ] Permissions audited: only CAMERA, (fine/coarse) LOCATION, POST_NOTIFICATIONS requested; no unused permissions in manifest
- [ ] Location permission usage matches an actual, obvious in-app feature (Play requires this for approval)
- [ ] Privacy Policy published at a public URL and linked in Play Console + in-app Settings
- [ ] Data Safety form completed per `DATA_SAFETY.md`, matching actual shipped behavior
- [ ] Content rating questionnaire completed (expected: general/everyone — no UGC sharing, no ads, no violence)
- [ ] App signing: enrolled in Play App Signing; upload key kept private, never committed to Git
- [ ] Release build is an **AAB**, built and tested via the release CI workflow
- [ ] Store listing assets ready: icon, feature graphic, phone screenshots (use dashboard/wizard/daily-entry screens), short & full description
- [ ] Any third-party SDK (maps, weather, AI, analytics/crash) reviewed for its own Play policy compliance and disclosed in Data Safety
- [ ] If cloud AI is included: disclosure text shown before first use, and Data Safety reflects it
- [ ] No crash-on-launch, no ANRs, tested on at least one low-end device/emulator profile
- [ ] Target-API extension requests (if ever needed) requested before Play's deadline, not after

## Explicitly out of scope for compliance claims
This document does not certify compliance — it is a working checklist. Final sign-off requires checking the live Play Console at submission time.
