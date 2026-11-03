# CircleCI compile error fixes

## Changes
1. Removed `app/src/main/java/com/bloodbridge/app/ui/screens/AdminShell.kt`.
   The app is client-only and this obsolete admin screen referenced ViewModel/repository
   properties and methods that are not part of the current client app, causing the
   unresolved-reference errors reported by CircleCI.
2. Fixed the stale `HomeScreen.kt` donor-search call to pass the required
   `upazila` argument: `vm.search(bloodGroup, district, "", nearMe)`.
3. Normalized `gradlew` to Unix LF line endings and kept it executable for Linux CI.

## Verification
- Confirmed no remaining `AdminShell` references in production Kotlin source.
- Confirmed all `vm.search(...)` calls now match the four-argument ViewModel signature.
- CircleCI logs supplied with this patch show the failing compilation originated in
  `AdminShell.kt` unresolved references. The HomeScreen call was also corrected.
- A local Gradle test run could not start because this environment cannot resolve
  `services.gradle.org` to download Gradle 8.9. Therefore a successful Gradle build
  or test run is NOT claimed. Please rerun CircleCI after pushing this source.
