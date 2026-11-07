# BloodBridge CircleCI Splash Resource Fix

## CircleCI logs reviewed
Both newly supplied logs fail at `:app:processDebugResources` with the same AAPT2 error in `app/src/main/res/drawable/bloodbridge_splash_background.xml`: a hex color (`#0B111B`) was assigned to `android:drawable`, which expects a drawable resource reference.

## Fix applied
- Replaced the invalid `<item android:drawable="#0B111B" />` with a nested `<color android:color="#0B111B" />` drawable item.
- Kept the launcher icon bitmap centered over the dark navy splash background.
- Kept AndroidX SplashScreen themes, dependency, and `installSplashScreen()` integration from the previous update.

## Static validation
- Parsed every XML file in this archive with Python XML parser.
- Confirmed the splash layer-list contains a valid color drawable and the launcher bitmap.
- Checked CircleCI YAML syntax when PyYAML is available.
- Verified ZIP CRC/integrity.

A full Gradle build was not run in this environment. The specific AAPT2 error in both supplied logs is fixed in this archive; CircleCI must run again to verify the complete build.
