# BloodBridge Android — Kotlin + Jetpack Compose

This is the **client-only Android app** for BloodBridge. It contains no admin dashboard or admin navigation. Existing donor/seeker workflows are retained: authentication, profile completion/editing, donor discovery, saved donors, blood requests, request responses, donor availability, donation history, organizations, in-app alerts, feedback, language/theme preferences and legal pages.

## 1. Open and run

1. Extract the project and open the `BloodBridge-Android` folder in Android Studio.
2. Use JDK 17 and let Android Studio sync the Gradle project.
3. Add your Firebase Android config at `app/google-services.json` (package: `com.bloodbridge.app`).
4. Set the environment values below in the root `gradle.properties` (do not commit real secrets or private configuration to a public repository).
5. Build > Make Project, then run on an emulator or Android device.

## 2. Backend and service configuration

The Android app does not use the web client's Vite `.env`. It reads Gradle project properties and exposes only the values needed by the app through generated `BuildConfig` constants.

```properties
# Root gradle.properties — replace with your real deployed API origin.
# Must include /api/ and end with a slash.
API_BASE_URL=https://YOUR-REAL-WORKER.workers.dev/api/

# Same public Cloudinary unsigned-upload configuration used by the web client.
CLOUDINARY_CLOUD_NAME=YOUR_CLOUDINARY_CLOUD_NAME
CLOUDINARY_UPLOAD_PRESET=YOUR_UNSIGNED_UPLOAD_PRESET

# Public website base URL (no trailing slash).
WEB_BASE_URL=https://YOUR_OFFICIAL_BLOODBRIDGE_SITE
```

For the Android emulator with a local backend, use `http://10.0.2.2:PORT/api/` and configure cleartext traffic only for a local debug build. Production must use HTTPS. Never put backend secrets, Firebase service-account credentials, private API keys or Cloudinary API secrets in the Android app; values compiled into an APK can be extracted. Firebase Android client configuration is not a service-account secret, but Firebase security must be enforced with Firebase/Backend rules.

`WEB_BASE_URL` is used for website legal-page links. The app also displays its Privacy Policy and Terms of Service offline inside the app. The web routes are `/privacy-policy` and `/terms`.

## 3. Firebase setup

- Create/register Android app with package `com.bloodbridge.app` in Firebase Console.
- Download `google-services.json` and place it in `app/`.
- Enable Email/Password and Google sign-in in Firebase Authentication if you use those flows.
- For Google sign-in, configure the Android SHA-1/SHA-256 fingerprints in Firebase and download the updated config file.

## 4. Features

- Email/password registration and login, Google sign-in, email verification, password reset and profile completion.
- Donor/seeker mode, donor search, saved donors and blood request workflows.
- Incoming, nearby and own requests; accept/reject/cancel actions.
- Availability, donation recording/history, organizations and in-app notifications.
- Profile editing, optional photo upload, account deletion, feedback submission/history.
- Bangla/English, light/dark theme, in-app Privacy Policy and Terms.

The app is intentionally client-only; admin screens are excluded.

## 5. Privacy policy notes

The in-app Privacy Policy is provided in English and Bangla and is available offline. It describes the current Android permissions and data flows. The current manifest requests Internet access only; it does not request GPS/location, contacts, microphone or camera permission. District/upazila/address are entered by the user. Profile photos are selected through Android's photo picker.

Before publishing, verify that the backend's actual data retention/deletion behavior, contact masking, Firebase setup, Cloudinary configuration and public website contact channel match the policy. If you add GPS, push notifications, analytics, ads or another SDK later, update the policy and permission disclosures before release.

## 6. Build verification

A successful ZIP integrity check is not an Android build test. Run Gradle Sync and `assembleDebug` in Android Studio or CI with a valid `google-services.json` and real `API_BASE_URL`. Then test login, registration, donor search, create/respond/cancel request, profile/photo update, feedback, notification reads and account deletion against the deployed backend.

The included GitHub Actions workflow uses Gradle 8.9 and JDK 17. Configure `GOOGLE_SERVICES_JSON_BASE64` and `API_BASE_URL` repository secrets before running it. Release signing additionally requires the documented keystore secrets.
