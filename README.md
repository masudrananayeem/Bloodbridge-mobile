# BloodBridge — native Android (Kotlin + Jetpack Compose)

Phase 1: Login, Register, Search donors (backend same: Cloudflare Worker + Firebase).
Phase 2+ (baki): dashboard, request pathano/incoming, availability + donation history + body animation,
notification, profile/photo, organizations, feedback, admin, BN/EN.

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
