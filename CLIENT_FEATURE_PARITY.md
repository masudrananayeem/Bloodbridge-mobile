# BloodBridge Android Client Feature Audit

## Scope
Compared the supplied React client source with the Kotlin/Jetpack Compose Android project. Admin-only pages and operations are intentionally excluded.

## Client features represented in Android
- Email/password sign-in and registration, Google sign-in, password reset, email verification, Google-created profile completion, optional registration profile photo.
- Donor/seeker mode switching.
- Donor search by blood group, district, and upazila; nearest-first API option; saved donors; direct and emergency requests; pagination. Donor cards show profile photo or initials, blood group, distance/age where available, and eligibility countdown.
- My, incoming, and nearby request lists; status filtering; accept/reject/cancel; accepted donor contact details; call shortcuts.
- Donor availability, eligibility countdown, donation recording, donation history, and statistics.
- Organization directory with district/type/text filters, organization details endpoint, phone dialer, email and website actions.
- Organization directory is accessible in both donor and seeker modes (seeker navigation gap corrected in this update).
- Profile editing, profile photo upload, password reset, account deletion, and logout.
- In-app notifications, unread count, mark-one/all-as-read; feedback categories and personal feedback history.
- Bangla/English, light/dark theme, privacy policy and terms of service.
- User-experience improvements: bottom navigation, mode-specific navigation, loading/error/empty states, resettable donor filters, organization search debounce, request status filter, and contact shortcuts.

## Clarifications / remaining work
1. **Push notifications (FCM):** the supplied web client implements an in-app notifications center using `/notifications`; it does not register an FCM token or implement browser push. FCM is therefore not required for parity with the supplied client, but it would be a separate enhancement requiring backend token registration and sending logic.
2. **Near-me behavior:** the supplied web client sends `nearMe=true` and describes results as nearest-first using the seeker's current/profile address. The Android app matches that API contract. It does not request live GPS coordinates; do not describe this as GPS proximity. A GPS-based feature would require runtime permission, coordinate query parameters, and backend support.
3. **Website marketing pages:** the website's landing page, testimonials, FAQ, and marketing sections are not reproduced in the native app; app users start at login/register. Core authenticated donor/seeker workflows are the focus.
4. **Build verification:** Gradle is unavailable in this environment and internet access to download it is blocked, so a real Android compile could not be run here. `google-services.json` is intentionally not included. Add Firebase configuration and set API/Cloudinary properties before runtime testing.

## Backend compatibility checks
- Donor search expects `page`, `limit` query parameters and a response with `donors`, `pages`, and `total`, as used by the supplied web client.
- Organization detail uses `GET /organizations/:id`, matching the supplied web service.
- The API base URL must be HTTPS in production and end with `/api/`.
- Profile photo upload requires Cloudinary cloud name and unsigned upload preset; Firebase authentication requires a valid `google-services.json` and enabled sign-in providers.

## Admin scope
No admin dashboard or admin CRUD features are included in the Android client app.
