# DATA_SAFETY.md — mapping to Google Play "Data safety" form

Fill this in the Play Console form as-is once features are final; verify against the *current* Play Console requirements before submitting (do not assume this list is complete — Google updates the form periodically).

## Data collected
| Category | Collected? | Shared with 3rd party? | Purpose | Optional? |
|---|---|---|---|---|
| Approximate/precise location | Yes (device only, farm GPS tagging) | Only sent to map/geocoding provider when you use map features | App functionality | Yes — GPS entry optional, map optional |
| Photos | Yes (local storage) | Only if you opt into AI analysis | App functionality | Yes |
| Financial info (wages, purchases, expenses) | Yes (local storage only) | No | App functionality | N/A — core feature, stays local |
| Personal info (worker names/phone, supervisor phone) | Yes (local storage only) | No | App functionality | N/A — stays local |
| App activity / crash logs | Only if analytics/crash SDK is added | Depends on SDK chosen | Analytics | Yes (should be off by default until decided) |

## Data deletion
Users can delete all app data via Android Settings → Apps → Farm Management → Clear data, or via in-app Settings → Backup & Restore → Delete local data.

## Encryption in transit
All external calls (maps, weather, geocoding, optional AI) must use HTTPS/TLS.

## Note
This document is a mapping aid, not a substitute for completing the actual Play Console "Data safety" questionnaire, which must reflect the final feature set (especially whether analytics/crash reporting is ultimately added).
