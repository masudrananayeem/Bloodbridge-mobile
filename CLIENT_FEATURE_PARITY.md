# BloodBridge Android — client feature parity

This native Android app is for end users only. It intentionally has **no admin panel** and no mobile admin CRUD screens/endpoints.

## Client-facing features included

- Email/password registration and login, Google sign-in, password reset, email verification, and completion of Google-created profiles.
- Donor / blood-seeker mode switch with mode-appropriate navigation.
- Donor search by blood group, district, and upazila; near-me-first filter; saved donor filtering; verified/available indicators; show-more pagination.
- Direct donor requests and emergency blood requests, including blood group, units, hospital, urgency, needed-by date, reason, and message.
- Request tracking, cancellation, incoming request accept/decline, nearby requests, accepted donor contact details, and request-linked notifications.
- Donor availability toggle, donation eligibility cooldown, donation recording, donation history, and donor dashboard statistics.
- Organization directory with district/type filters and tap-to-call.
- Profile editing, age calculation from date of birth, profile photo upload (Cloudinary), password reset, logout, and account deletion with confirmation.
- Notification list, unread count, mark-one-read, mark-all-read.
- Bangla/English language toggle, light/dark theme, and in-app privacy/terms links.
- Feedback and support form with General, Bug Report, Feature Request, Complaint, and Praise categories, plus the user's feedback history.

## Setup requirements

1. Open this folder in Android Studio.
2. Add your Firebase Android configuration at `app/google-services.json`; enable Email/Password and Google sign-in in Firebase.
3. Set `API_BASE_URL` in `gradle.properties` to your deployed API base URL, ending in `/api/`.
4. Optional profile-photo uploads require `CLOUDINARY_CLOUD_NAME` and `CLOUDINARY_UPLOAD_PRESET`.
5. Build/run from Android Studio. This source ZIP did not include the Gradle wrapper script or Firebase configuration file, so those setup steps are required before a local build.

## Notes

- The app uses the existing BloodBridge API. Feedback expects `POST /api/feedback` with `{ category, message }` and `GET /api/feedback/my`, matching the supplied web client service.
- Push notifications via Firebase Cloud Messaging are not implemented in this app; the in-app notification center is included.
- This environment did not have a Gradle executable or wrapper script available, so an Android build could not be run here.
