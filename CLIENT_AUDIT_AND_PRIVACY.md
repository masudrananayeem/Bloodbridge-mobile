# BloodBridge Android client audit — 4 November 2026

## Changes made during this audit

- Fixed the CircleCI Kotlin compile failure in `HomeScreen.kt`: the donor search call now passes the required `upazila` string before the `nearMe` boolean, matching `AppViewModel.search(bloodGroup, district, upazila, nearMe)`.
- Added the missing upazila filter field to the legacy `HomeScreen` so its search form matches the ViewModel signature.
- Added the Organizations destination to seeker-mode bottom navigation as well as donor mode.
- Added text search to the organization directory and exposed organization email and website actions alongside the existing phone action.
- Removed the unused admin dashboard screen, admin-only ViewModel operations, repository methods, API endpoints and DTOs. The Android app remains client-only.
- Normalized the Unix `gradlew` script to LF line endings and added `.gitattributes` to prevent Windows line-ending conversion from breaking Linux CI.
- Corrected the feature audit: donor search currently has no server-side pagination (`page`/`limit` are not sent by Android and the response DTO only models `donors`). The existing “Show more” behavior is local display expansion only, not API pagination.
- Corrected the feature audit: GPS coordinates are not collected or sent. `nearMe` is an API sort/filter flag based on the service's existing user/profile context; it is not device GPS proximity.
- Clarified that organization detail navigation is not a separate endpoint/screen. The directory response itself contains the details currently displayed by the app.

## Current Android client feature coverage

- Firebase email/password authentication, registration, password reset and email verification.
- Google sign-in through Credential Manager/Firebase, followed by profile completion when required.
- Donor/seeker mode switching.
- Donor discovery filters: blood group, district, upazila and `nearMe` API flag; saved donors; direct and emergency blood requests.
- My, incoming and nearby request lists; accept/reject/cancel actions and request status display.
- Donor availability, donation recording, eligibility countdown and donation history.
- Organization directory with district/type/text filters and phone/email/website actions.
- Profile editing, optional profile-photo upload through Cloudinary, account deletion and logout.
- In-app notifications and read/unread actions; feedback submission and personal feedback history.
- Bangla/English language preference, light/dark theme and in-app Privacy Policy/Terms.

## Known gaps / items requiring backend or product confirmation

1. **Server-side donor pagination:** not implemented in this Android source. Add only after confirming the backend query/response contract (`page`, `limit`, `pages`, `total`) and matching the web client contract.
2. **Live GPS nearby search:** not implemented. The manifest requests Internet only; no location permission or coordinates are collected.
3. **Push notifications:** the app has an in-app notification center. FCM device-token registration/background push delivery is not implemented and is not claimed as complete.
4. **Organization detail screen:** the app displays available organization fields in directory cards; no dedicated detail route is present.
5. **Website-only marketing pages:** landing page, FAQ and testimonials are not native app screens.
6. **Runtime integration:** Firebase providers, Cloudinary unsigned preset, production API URL, backend response compatibility and account-deletion behavior must be tested against the deployed services.

## Verification performed for this update

- Confirmed the reported `HomeScreen.kt:79` call-site mismatch against the actual ViewModel method signature and corrected it.
- Parsed `.circleci/config.yml` as YAML successfully.
- Checked the Gradle wrapper JAR SHA-256 against the checksum expected by CircleCI.
- Checked the POSIX wrapper script syntax and normalized its line endings.
- Full Gradle tasks (`testDebugUnitTest`, `lintDebug`, `assembleDebug`) could not be run in this environment because `services.gradle.org` could not be resolved (network/DNS unavailable). Therefore no successful Android build is claimed here.

## Privacy and release notes

- The Android manifest currently requests Internet permission only. No location, contacts, camera or microphone permission is requested.
- Firebase Android client configuration is not a service-account credential, but Firebase rules and backend authorization must enforce access controls. Do not embed service-account keys, private API keys, Cloudinary API secrets or signing credentials in the APK.
- Cloudinary cloud name and unsigned upload preset are client-visible configuration; restrict the preset's allowed formats, size and upload behavior in Cloudinary.
- Confirm the in-app Privacy Policy against actual backend retention/deletion behavior and publish a working public policy URL/support contact before Play Store release.
