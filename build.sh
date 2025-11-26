#!/usr/bin/env zsh
# build.sh - compile, package and run DetrivoreAttack
# Usage: ./build.sh [build|jar|run|clean]
set -euo pipefail
PROJECT_ROOT=$(cd "$(dirname "$0")" && pwd)
SRC_DIR="$PROJECT_ROOT/src"
BIN_DIR="$PROJECT_ROOT/bin"
RES_DIR="$PROJECT_ROOT/resources"
JAR_NAME="DetrivoreAttack.jar"
MANIFEST_FILE="$PROJECT_ROOT/manifest.txt"

function do_build() {
  echo "==> Cleaning and creating bin/"
  rm -rf "$BIN_DIR"
  mkdir -p "$BIN_DIR"

  echo "==> Compiling Java sources"
  # compile all .java files found under src into bin
  find "$SRC_DIR" -name '*.java' -print0 | xargs -0 javac -d "$BIN_DIR"

  echo "==> Copying resources into bin/ (so ClassLoader can find sprites/sounds)"
  if [ -d "$RES_DIR" ]; then
    cp -R "$RES_DIR"/* "$BIN_DIR/" || true
  else
    echo "Warning: resources/ not found; make sure sprites/ and sounds/ are on classpath or packaged into the jar"
  fi

  echo "==> Writing manifest (Main-Class: Game)"
  printf "Main-Class: Game\n" > "$MANIFEST_FILE"

  echo "==> Creating jar: $JAR_NAME"
  jar cfm "$PROJECT_ROOT/$JAR_NAME" "$MANIFEST_FILE" -C "$BIN_DIR" .

  echo "Build complete: $PROJECT_ROOT/$JAR_NAME"
}

function do_run() {
  # Ensure save dir exists next to jar (Game uses user.dir/save)
  mkdir -p "$PROJECT_ROOT/save"
  echo "Running jar... (working dir: $PROJECT_ROOT)"
  (cd "$PROJECT_ROOT" && java -jar "$JAR_NAME")
}

function do_clean() {
  echo "Cleaning build artifacts"
  rm -rf "$BIN_DIR" "$PROJECT_ROOT/$JAR_NAME" "$MANIFEST_FILE"
  echo "Clean complete"
}

case "${1:-build}" in
  build)
    do_build
    ;;
  jar)
    do_build
    ;;
  run)
    if [ ! -f "$PROJECT_ROOT/$JAR_NAME" ]; then
      echo "Jar not found, building first..."
      do_build
    fi
    do_run
    ;;
  clean)
    do_clean
    ;;
  *)
    echo "Usage: $0 [build|jar|run|clean]"
    exit 1
    ;;
esac
