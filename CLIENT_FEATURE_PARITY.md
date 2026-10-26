# BloodBridge Android Client Feature Audit

## Scope
Compared the supplied React client source with the Kotlin/Jetpack Compose Android project. Admin-only pages and operations are intentionally excluded from the Android app.

## Present in the Android app
- Email/password sign-in and registration, Google sign-in, password reset, email verification, and completion of Google-created profiles.
- Donor/seeker mode switching.
- Donor search by blood group, district, and upazila; near-me-first option; saved-donor list/filter; direct requests; emergency requests.
- Request list, incoming and nearby requests, accept/decline, cancel, status display, accepted donor contact details.
- Donor availability, eligibility countdown, record donation, donation history and statistics.
- Organization directory with district/type filters, phone dialer, email action, website action, and text search.
- Profile update, profile photo upload, password reset, account deletion, logout.
- Notifications center, unread count, mark one/all as read.
- Bangla/English, light/dark theme, privacy policy and terms links.
- Feedback categories and personal feedback history.
- Added/strengthened mobile UX: app header with unread notification shortcut, clearer donor-search helper text, reset filters, server-side donor pagination, request-status filter, organization text search and organization email/website shortcuts, tap-to-call for request contacts.

## Gaps / items requiring follow-up
1. **Push notifications (FCM): not implemented.** The in-app notifications screen works through the API, but the app does not register an FCM token or receive push messages.
2. **Geolocation permission / GPS-based nearby sorting:** the app sends the API's `nearMe` filter, but it does not request Android location permission or pass live coordinates. Confirm whether the backend's nearMe behavior uses profile location; true GPS proximity needs explicit location integration.
3. **Organization details:** the web client has a `GET /organizations/:id` service, while the app shows organization details directly on list cards and does not call the detail endpoint. Contact shortcuts are included.
4. **Landing/marketing pages:** the website's public landing page, testimonials, FAQ, and marketing sections are not reproduced in the native app. The app starts at login/register; add an onboarding/landing screen only if you want website marketing content in mobile too.
5. **Build verification:** not verified in this environment because Gradle and the Gradle wrapper are unavailable and `google-services.json` is not included. Run Gradle sync/build in Android Studio after adding Firebase configuration and setting API/cloud configuration.

## Backend compatibility checks needed
- Ensure the donor-search endpoint accepts `page` and `limit` and returns `donors`, `pages`, and `total` as the supplied web client expects.
- Confirm all Android API paths/payloads match the deployed backend version.
- Keep `API_BASE_URL` pointed at HTTPS in production; the default emulator URL is for local development only.

## Admin scope
No admin dashboard or admin CRUD features are included in the Android client app.
