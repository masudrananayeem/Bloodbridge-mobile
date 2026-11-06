# BloodBridge Android — Kotlin + Jetpack Compose

This is the **client-only Android app** for BloodBridge. It contains no admin dashboard or admin navigation. Existing donor/seeker workflows are retained: authentication, profile completion/editing, donor discovery, saved donors, blood requests, request responses, donor availability, donation history, organizations, in-app alerts, feedback, language/theme preferences and legal pages.

## 1. Open and Run Locally

1. Extract the project and open the repository root in Android Studio.
2. Use JDK 17 and let Android Studio sync the Gradle project.
3. Add your Firebase Android config at `app/google-services.json` (package: `com.bloodbridge.app`).
4. Set the environment values below in the root `gradle.properties` (do not commit real secrets or private configuration to a public repository).
5. Build > Make Project, then run on an emulator or Android device

## 2. Backend and Service Configuration

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

For the Android emulator with a local backend, use `http://10.0.2.2:PORT/api/` and configure cleartext traffic only for a local debug build. Production release builds require an HTTPS API base URL. Never put backend secrets, Firebase service-account credentials, private API keys, or Cloudinary API secrets in the Android app; values compiled into an APK can be extracted. Firebase Android client configuration is not a service-account secret, but Firebase security must be enforced with Firebase/Backend rules.

`WEB_BASE_URL` is used for website legal-page links. The app also displays its Privacy Policy and Terms of Service offline inside the app. The web routes are `/privacy-policy` and `/terms`.

## 3. Firebase Setup

- Create/register Android app with package `com.bloodbridge.app` in Firebase Console.
- Download `google-services.json` and place it in `app/`.
- Enable Email/Password and Google sign-in in Firebase Authentication if you use those flows.
- For Google sign-in, configure the Android SHA-1/SHA-256 fingerprints in Firebase and download the updated config file.

## 4. CircleCI CI/CD Pipeline

**Maintainer**: `mosabbir-maruf`

The project uses CircleCI 2.1 as its single CI/CD build and test automation platform. All CI/CD operations execute on CircleCI-managed cloud infrastructure without requiring local build tools on your development VPS.

### Pipeline Architecture

The pipeline consists of two decoupled workflows:

1. **`build_and_test` (Pull Requests & Branches)**:
   - `prepare_and_validate`: Validates environment variables, checks Gradle wrapper integrity, securely decodes `google-services.json`, warms up Gradle dependency cache.
   - `unit_tests`: Executes JUnit 4 unit tests (`./gradlew testDebugUnitTest`) and collects XML/HTML test reports.
   - `android_lint`: Runs static analysis (`./gradlew lintDebug`) and collects lint reports.
   - `debug_apk`: Assembles debug APK (`./gradlew assembleDebug`), calculates SHA-256 checksum, generates build metadata.
   - `artifact_validation`: Verifies APK integrity, validates package name `com.bloodbridge.app`, and uploads `app-debug.apk` as downloadable artifact.

2. **`release_pipeline` (Version Tags `v*`)**:
   - Executes `prepare_and_validate`, `unit_tests`, and `android_lint`.
   - `release_apk`: Validates release keystore credentials, decodes keystore in temporary secure storage with auto-cleanup trap, builds signed release APK (`./gradlew assembleRelease`), verifies signature with `apksigner`, computes SHA-256 checksum, generates `build-info.txt`.
   - `artifact_validation`: Verifies release APK integrity, verifies SHA-256 checksum, and uploads `app-release.apk`, `app-release.apk.sha256`, and `build-info.txt`.

### Connecting GitHub Repository to CircleCI

1. Log into [CircleCI](https://circleci.com/) using your GitHub account.
2. In the CircleCI Dashboard, select **Projects** and find `Bloodbridge-mobile`.
3. Click **Set Up Project** and choose the existing `.circleci/config.yml` on the `main` branch.
4. Navigate to **Project Settings > Environment Variables** and add the required environment variables.

### Required CircleCI Environment Variables

Configure these in **Project Settings > Environment Variables**:

| Variable | Required For | Description |
|---|---|---|
| `GOOGLE_SERVICES_JSON_BASE64` | All builds | Base64-encoded `google-services.json` (`base64 -w 0 app/google-services.json`) |
| `API_BASE_URL` | All builds / Release | Backend API origin (e.g. `https://api.yourdomain.com/api/`). Required for release builds. |
| `CLOUDINARY_CLOUD_NAME` | Optional | Public Cloudinary cloud name for profile photo uploads. |
| `CLOUDINARY_UPLOAD_PRESET` | Optional | Public Cloudinary upload preset. |
| `WEB_BASE_URL` | Optional | Public website base URL for legal links (defaults to `https://blood-bridge.pages.dev`). |
| `ANDROID_KEYSTORE_BASE64` | Release (`v*`) | Base64-encoded release `.jks` keystore (`base64 -w 0 release.jks`). |
| `ANDROID_KEYSTORE_PASSWORD` | Release (`v*`) | Password for the release keystore. |
| `ANDROID_KEY_ALIAS` | Release (`v*`) | Key alias inside the keystore. |
| `ANDROID_KEY_PASSWORD` | Release (`v*`) | Password for the key alias. |

### How to Trigger Builds

- **Debug Build**: Push a commit to any branch or open a Pull Request. CircleCI automatically runs validation, unit tests, lint, and builds `app-debug.apk`.
- **Release Build**: Push a git tag starting with `v` (e.g., `v1.0.0`, `v1.0.1`):
  ```bash
  git tag -a v1.0.0 -m "Release v1.0.0"
  git push origin v1.0.0
  ```

### How to Download APK Artifacts

1. Go to the [CircleCI App](https://app.circleci.com/) and open your pipeline run.
2. Click on the completed **`artifact_validation`** job.
3. Switch to the **Artifacts** tab.
4. Download:
   - For debug: `build-artifacts/app-debug.apk` and `build-artifacts/build-info.txt`.
   - For release: `build-artifacts/app-release.apk`, `build-artifacts/app-release.apk.sha256`, and `build-artifacts/build-info.txt`.

### Release Keystore Configuration

To generate a new release keystore locally for CI signing:

```bash
keytool -genkeypair -v -keystore release.jks -alias bloodbridge -keyalg RSA -keysize 2048 -validity 10000
```

Encode the keystore to base64:

```bash
base64 -w 0 release.jks
```

Copy the base64 output into the `ANDROID_KEYSTORE_BASE64` environment variable in CircleCI. Never commit `.jks` files to git.

### Troubleshooting Common CI Failures

1. **`GOOGLE_SERVICES_JSON_BASE64 is not set`**:
   Ensure the variable is added in CircleCI Project Settings > Environment Variables without surrounding quotes.
2. **`API_BASE_URL must be specified for release builds`**:
   Release builds require a valid non-empty `API_BASE_URL` environment variable.
3. **`Release signing credentials missing`**:
   Verify that all four signing variables (`ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`) are configured.
4. **`gradle-wrapper.jar SHA-256 does not match official release`**:
   Ensure `gradle/wrapper/gradle-wrapper.jar` has not been corrupted or tampered with. The official SHA-256 for Gradle 8.9 wrapper jar is `498495120a03b9a6ab5d155f5de3c8f0d986a449153702fb80fc80e134484f17`.
5. **Out of memory during Gradle build**:
   The CircleCI executor uses `resource_class: large` with `Xmx2048m`. If your build expands significantly, increase the resource class or JVM heap size in `.circleci/config.yml`.

## 5. Automated Tests

Automated unit tests cover pure business logic, calculations, data serialization, and input validations without Android device emulator overhead:

- `EligibilityTest`: Tests donation cooldown (`DONATION_COOLDOWN_DAYS = 120`), `daysUntilEligible`, `nextEligibleLabel`, `shortDate`, and `ageFromDob`.
- `ModelsSerializationTest`: Tests `AppJson` serialization/deserialization for `User`, `Donor`, `BloodRequest`, `ErrorResponse`, null-safety, and unknown JSON fields.
- `FriendlyMessageTest`: Tests API error-to-user-message translations and network error handling.
- `ValidationTest`: Tests Bangladeshi mobile phone regex (`^01[3-9]\d{8}$`), ISO date of birth regex (`^\d{4}-\d{2}-\d{2}$`), and standard `Constants` (blood groups, 64 districts, statuses).
- `LocalizationTest`: Tests Bengali/English translations and fallback mechanisms.

Execute tests locally with:
```bash
./gradlew testDebugUnitTest
```

## 6. Security Notes

- In-app Privacy Policy is offline and describes permissions (Internet only).
- Release signing credentials exist exclusively in CircleCI secrets and temporary memory during release assembly, wiped immediately after use.
- GitHub Actions Android build workflow has been decommissioned in favor of CircleCI.
