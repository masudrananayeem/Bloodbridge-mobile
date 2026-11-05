# BloodBridge Android UI Refresh

## What changed
- Refreshed Material 3 light/dark color palettes with a richer navy surface system and clearer blood-red accent.
- Added a branded login hero panel and improved login hierarchy.
- Redesigned the authenticated app header and donor/seeker mode switch.
- Refined bottom navigation selection treatment.
- Harmonized shared text-field/button shapes and feature-card corner radii.

## Functionality preserved
No API routes, repository methods, authentication flows, ViewModel actions, navigation destinations, or data models were intentionally changed. Existing tabs and actions remain wired to the same ViewModel methods.

## Build
Use the existing CircleCI Android workflow. Ensure the CircleCI project environment variable `API_BASE_URL` remains:
`https://blood-bridge-server.masudrananayeem86.workers.dev/api/`

This source refresh has not been compiled locally in this environment. Run the CircleCI pipeline and review Kotlin compile/lint results before distributing the APK.
