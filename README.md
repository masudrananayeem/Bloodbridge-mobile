# BloodBridge Android — Kotlin + Jetpack Compose

Native Android client for the BloodBridge service. The admin panel and admin-only CRUD screens/endpoints are intentionally excluded.

## Client features
- Firebase email/password auth, Google sign-in, verification and password reset.
- Donor/seeker mode switching, donor search and server-side pagination, saved donors, emergency/direct requests.
- My/incoming/nearby requests, status filtering, cancel/accept/decline and call shortcuts for available request contacts.
- Donor availability, eligibility countdown, donation recording/history and dashboard statistics.
- Organization directory with district/type/text filters and call/email/website actions in both donor and seeker modes.
- Profile editing, profile image upload, account deletion, notification center, feedback history, Bangla/English, light/dark theme and legal links.

See `CLIENT_FEATURE_PARITY.md` for the feature audit and known gaps.

## Setup
1. Open this folder in Android Studio.
2. Add Firebase's `google-services.json` at `app/google-services.json`; enable Email/Password and Google sign-in in Firebase.
3. Set `API_BASE_URL` in `gradle.properties` to the deployed API URL ending with `/api/`.
4. Optional profile image upload requires `CLOUDINARY_CLOUD_NAME` and `CLOUDINARY_UPLOAD_PRESET` in `gradle.properties`.
5. Sync Gradle and build/run from Android Studio. The project uses Gradle 8.9 / Android Gradle Plugin 8.7.3 and Java 17.
6. For optional profile photo upload, configure the Cloudinary cloud name and unsigned upload preset. Do not commit credentials or Firebase configuration to a public repository.

For Google sign-in, add the Android app's SHA-1/SHA-256 fingerprints in Firebase and download a fresh `google-services.json`.

## Important verification notes
- `google-services.json` is intentionally not included. Add Firebase configuration before running auth flows. The included `gradlew` / `gradlew.bat` bootstrap scripts download the official Gradle 8.9 distribution over HTTPS on first run because the standard wrapper JAR is not included. The CI workflow can compile without Firebase config when the secret is absent, but runtime authentication cannot work until configuration is supplied.
- A full Android compile could not be run in this environment because Gradle is unavailable and network access to download it is blocked. Build in Android Studio after completing setup.
- Donor search now sends `page` and `limit` and expects the API response fields `donors`, `pages`, and `total`, matching the supplied web client. Donor cards show profile photos or initials and eligibility countdowns when supplied by the API.
- Use HTTPS for production API traffic. The default API URL is a placeholder (`https://YOUR-WORKER.workers.dev/api/`). Replace it with your deployed HTTPS API URL before running the app.
