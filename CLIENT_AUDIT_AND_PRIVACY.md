# BloodBridge Android client audit — 30 October 2026

## Changes in this update

- Replaced the short in-app Privacy Policy with a fuller English and Bangla policy based on the actual Android app data model, API calls and manifest.
- Clarified that the current Android app requests Internet permission only; it does not request GPS/location, contacts, camera or microphone permissions. District/upazila/address are user-entered fields.
- Clarified Firebase Authentication, Cloudinary profile-photo upload, user-visible donor profile fields, request contact sharing, in-app notification behavior, account deletion and security limitations.
- Corrected the Terms website route from `/terms-of-service` to the actual web route `/terms`.
- Removed the admin dashboard entry point and admin screen file from the Android client. Admin accounts are not routed to an Android admin panel.
- Added the client web app's Feedback feature to Android: category + message submission (`POST /feedback`) and personal history (`GET /feedback/my`).
- Reworked README setup instructions for backend URL, Firebase config, Cloudinary and website URL.

## Important verification notes

- The source ZIP can be checked for archive integrity, but this environment does not have the Android SDK/Gradle installation or the project's `google-services.json`, so an Android compile/runtime test has not been performed here.
- The project currently contains Gradle wrapper properties but not the wrapper JAR/scripts. Use Android Studio's configured Gradle installation or the included GitHub Actions workflow (which installs Gradle 8.9) to compile. Do not treat archive integrity as a successful build.
- Before publishing, confirm that the deployed backend's data visibility, deletion and retention behavior matches the policy. Also provide a working public privacy-policy URL and support contact channel.
- GPS and push notifications are not claimed as implemented. If either is added later, update permissions, disclosures and this policy before release.
