# BloodBridge launch splash screen

Added AndroidX SplashScreen support. The launch screen uses the existing BloodBridge launcher icon on a deep navy background (#0B111B). Android 12+ uses the platform splash API; older Android versions use a theme window background. Existing authentication and navigation logic is unchanged.

CircleCI must resolve androidx.core:core-splashscreen:1.0.1 during Gradle dependency resolution.
