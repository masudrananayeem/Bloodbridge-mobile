# BloodBridge CircleCI Splash Screen Fix

## Findings from supplied CircleCI logs
Both supplied build logs fail at `:app:mergeDebugResources` while parsing
`app/src/main/res/values-v31/themes.xml` with:
`The processing instruction target matching "[xX][mM][lL]" is not allowed.`

This is an XML resource parsing failure, not a Kotlin or Firebase error.

## Changes
- Reworked `values/themes.xml` and `values-v31/themes.xml` to use the
  AndroidX `Theme.SplashScreen` parent.
- Removed XML declarations from both values resource files (Android resource
  XML does not need them); this also prevents the XML processing-instruction
  parser failure seen in the supplied CI log.
- Corrected `postSplashScreenTheme` to the AndroidX attribute name (no
  `android:` prefix).
- Kept the app's existing dark background and launcher icon as the splash icon.
- Kept the existing `androidx.core:core-splashscreen:1.0.1` dependency and
  `installSplashScreen()` call; no duplicate dependency was added.

## Validation performed here
- Parsed all XML files in the project archive with Python's XML parser.
- Checked the two modified theme files for a single root and no XML
  processing-instruction declarations.
- Checked CircleCI YAML syntax when a YAML parser is available.
- Checked ZIP integrity.

A full Gradle/Android build was not run in this environment. Therefore this
archive is statically inspected and the reported resource error is addressed,
but a green CircleCI build cannot be guaranteed until CircleCI runs it.
