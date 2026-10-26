# BloodBridge — native Android (Kotlin + Jetpack Compose)

Phase 1: Login, Register, Search donors.
Phase 2: bottom tabs — Search (+ direct request, emergency request), Requests (My / Incoming accept-decline / Nearby),
Donor (availability, "I donated today", blood-level body animation, history), Alerts (notifications), Profile.
Phase 3: profile edit + photo upload (Cloudinary), organizations directory (call button), forgot password, delete account.
  -> gradle.properties e CLOUDINARY_CLOUD_NAME ar CLOUDINARY_UPLOAD_PRESET dite hobe (web-er VITE_CLOUDINARY_* er same value).
Phase 4: Bangla/English toggle (login screen + Profile), Google login + complete-profile step, admin panel
  (admin role-er account login korle automatic AdminShell: Overview stats, Users verify/delete/search/load-more, Requests status).
  Google login: Firebase Console > Authentication > Google enabled; Project settings > Android app e SHA-1 (+ SHA-256) add
  kore google-services.json abar download kore app/ e rakhun. (debug SHA-1: ./gradlew signingReport)
Phase 5: Donor/Seeker mode switch (mode-wise tabs), email verification banner, saved donors, show more, organizations CRUD + analytics (admin), theme toggle, alert tap -> Requests.
Baki: feedback form (feedback API-er path/body dorkar), push notification (FCM + backend change), seeker contact after accept.

## Run (VS Code-e code dekhben, build Android Studio-te)
1. Android Studio install (developer.android.com/studio). File > Open > ei folder.
2. Firebase Console > Project settings > Add app > Android, package: com.bloodbridge.app
   - google-services.json download kore  app/google-services.json  e rakhun.
   - Authentication > Email/Password enabled.
3. gradle.properties e API_BASE_URL set korun (shesh-e / thakte hobe):
       API_BASE_URL=https://YOUR-WORKER.workers.dev/api/
   Local backend + emulator: http://10.0.2.2:5000/api/   (cleartext http-er jonno manifest-e
   android:usesCleartextTraffic="true" lagbe — shudhu debug-er jonno)
4. Sync > Run (emulator ba USB phone).

## Build without Android Studio (GitHub Actions)
Secrets: GOOGLE_SERVICES_JSON_BASE64, API_BASE_URL, (release-er jonno) ANDROID_KEYSTORE_BASE64,
ANDROID_KEYSTORE_PASSWORD, ANDROID_KEY_ALIAS, ANDROID_KEY_PASSWORD.
Actions > "Android build" > Run workflow > artifact: debug APK / release AAB.
Build fail korle log-er error pathan.

## Backend CORS
Native app browser na, tai CORS lage na.
