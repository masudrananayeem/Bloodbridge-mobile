# BloodBridge Android — Web UI Alignment Update

## Visual direction
The Android client has been visually aligned with the supplied BloodBridge web screenshots:
- Near-black / deep navy app background and charcoal surfaces
- BloodBridge red gradient hero panels
- Branded heart mark and consistent app header
- Donor and seeker mode switch retained
- Role-specific dashboard welcome panels
- Organization directory search and organization website links

## Preserved behavior
Existing Firebase authentication, Cloudflare API calls, donor availability, donation recording/history,
seeker donor search filters, saved donors, emergency requests, request tabs/actions, organizations,
notifications, profile, language, theme, and legal links have not intentionally been removed or replaced.

## Organization website behavior
Organization cards now show a `Visit website` action when `website` is present.
The app normalizes bare domains to HTTPS and opens the link with Android ACTION_VIEW.
Phone and email details remain visible; the existing call action remains.

## Build configuration
`gradle.properties` retains:
`API_BASE_URL=https://blood-bridge-server.masudrananayeem86.workers.dev/api/`

CircleCI should continue to supply the same value as `API_BASE_URL` in Project Settings > Environment Variables.
The source was not compiled in this environment because the Gradle distribution download could not resolve
`services.gradle.org`; run the CircleCI pipeline to perform the authoritative Android compile, lint, and APK checks.
