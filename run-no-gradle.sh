#!/usr/bin/env bash
set -euo pipefail

# Run MealMotion without Gradle.
# Requires: JDK 17+ (javac/java on PATH)

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"

MAIN_CLASS="mealmotion.MealMotionApp"
SRC_DIR="src/main/java"
RES_DIR="src/main/resources"

OUT_DIR="out"
CLASSES_DIR="$OUT_DIR/classes"

if ! command -v javac >/dev/null 2>&1; then
  echo "Error: 'javac' not found. Install JDK 17+ and ensure it's on PATH." >&2
  echo "On macOS you can also check: /usr/libexec/java_home -V" >&2
  exit 1
fi

mkdir -p "$CLASSES_DIR"

echo "Compiling Java sources to $CLASSES_DIR ..."
find "$SRC_DIR" -name "*.java" -print0 | xargs -0 javac --release 17 -d "$CLASSES_DIR"

if [ -d "$RES_DIR" ]; then
  echo "Copying resources from $RES_DIR ..."
  cp -R "$RES_DIR"/. "$CLASSES_DIR"/
fi

echo "Launching $MAIN_CLASS ..."
exec java -cp "$CLASSES_DIR" "$MAIN_CLASS"

