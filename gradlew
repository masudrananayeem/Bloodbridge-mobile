#!/usr/bin/env sh
# BloodBridge Gradle bootstrap for environments where the standard wrapper JAR is not present.
# Downloads the official Gradle distribution over HTTPS on first run, then executes it.
set -eu
GRADLE_VERSION="8.9"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
INSTALL_DIR="$GRADLE_USER_HOME/bloodbridge-bootstrap/gradle-$GRADLE_VERSION"
GRADLE_BIN="$INSTALL_DIR/gradle-$GRADLE_VERSION/bin/gradle"
if [ ! -x "$GRADLE_BIN" ]; then
  command -v curl >/dev/null 2>&1 || { echo "curl is required to bootstrap Gradle $GRADLE_VERSION." >&2; exit 1; }
  command -v unzip >/dev/null 2>&1 || { echo "unzip is required to bootstrap Gradle $GRADLE_VERSION." >&2; exit 1; }
  mkdir -p "$INSTALL_DIR"
  ARCHIVE="$INSTALL_DIR/gradle-$GRADLE_VERSION-bin.zip"
  echo "Downloading official Gradle $GRADLE_VERSION distribution..."
  curl --fail --location --retry 2 "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" --output "$ARCHIVE"
  unzip -q -o "$ARCHIVE" -d "$INSTALL_DIR"
  rm -f "$ARCHIVE"
fi
exec "$GRADLE_BIN" "$@"
