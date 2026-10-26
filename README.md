# BloodBridge Android — Kotlin + Jetpack Compose

Native Android client for the BloodBridge service. The admin panel and admin-only CRUD screens/endpoints are intentionally excluded.

## Client features
- Firebase email/password auth, Google sign-in, verification and password reset.
- Donor/seeker mode switching, donor search and server-side pagination, saved donors, emergency/direct requests.
- My/incoming/nearby requests, status filtering, cancel/accept/decline and call shortcuts for available request contacts.
- Donor availability, eligibility countdown, donation recording/history and dashboard statistics.
- Organization directory with district/type/text filters and call/email/website actions.
- Profile editing, profile image upload, account deletion, notification center, feedback history, Bangla/English, light/dark theme and legal links.

See `CLIENT_FEATURE_PARITY.md` for the feature audit and known gaps.

## Setup
1. Open this folder in Android Studio.
2. Add Firebase's `google-services.json` at `app/google-services.json`; enable Email/Password and Google sign-in in Firebase.
3. Set `API_BASE_URL` in `gradle.properties` to the deployed API URL ending with `/api/`.
4. Optional profile image upload requires `CLOUDINARY_CLOUD_NAME` and `CLOUDINARY_UPLOAD_PRESET` in `gradle.properties`.
5. Sync Gradle and build/run from Android Studio.

For Google sign-in, add the Android app's SHA-1/SHA-256 fingerprints in Firebase and download a fresh `google-services.json`.

## Important verification notes
- `google-services.json` and a Gradle wrapper are not included in this source ZIP.
- The source could not be built in this environment because Gradle is unavailable. Build in Android Studio after completing setup.
- Donor search now sends `page` and `limit` and expects the API response fields `donors`, `pages`, and `total`, matching the supplied web client.
- Use HTTPS for production API traffic. The `http://10.0.2.2:5000/api/` URL is only for a local backend on an Android emulator and requires cleartext traffic to be enabled for debug builds.
