#!/usr/bin/env bash
# Shared preflight + command runner for the macOS and Linux clickable scripts.
# Usage: bash run.sh <check|start|verify|build>
set -uo pipefail

COMMAND="${1:-check}"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$HERE/../.." && pwd)"
cd "$ROOT" || { printf '[UHS] ERROR: project folder not found: %s\n' "$ROOT" >&2; exit 1; }

say() { printf '\n[UHS] %s\n' "$*"; }
die() { printf '\n[UHS] ERROR: %s\n' "$*" >&2; exit 1; }

read_cfg() {
  local key="$1" file="$2" line value
  [ -f "$file" ] || return 1
  line="$(grep -E "^[[:space:]]*${key}[[:space:]]*=" "$file" | tail -n 1)"
  [ -n "$line" ] || return 1
  value="${line#*=}"
  value="${value#"${value%%[![:space:]]*}"}"
  value="${value%"${value##*[![:space:]]}"}"
  value="${value#\"}"; value="${value%\"}"
  value="${value#\'}"; value="${value%\'}"
  [ -n "$value" ] && printf '%s' "$value"
}

say "Project folder: $ROOT"

# Java 25. macOS: java_home. Linux: JAVA_HOME or a JDK 25 in common folders.
say "Checking JDK 25 ..."
if command -v /usr/libexec/java_home >/dev/null 2>&1; then
  JDK25="$(/usr/libexec/java_home -v 25 2>/dev/null || true)"
  if [ -n "$JDK25" ]; then
    export JAVA_HOME="$JDK25"
    export PATH="$JAVA_HOME/bin:$PATH"
  fi
else
  if [ -z "${JAVA_HOME:-}" ] || [ ! -x "$JAVA_HOME/bin/java" ]; then
    for dir in /usr/lib/jvm/*25* /opt/java/*25* /opt/jdk*25* \
                "$HOME/.sdkman/candidates/java/"*25* "$HOME/.jdks/"*25*; do
      [ -d "$dir" ] && [ -x "$dir/bin/java" ] || continue
      export JAVA_HOME="$dir"
      break
    done
  fi
  if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    export PATH="$JAVA_HOME/bin:$PATH"
  fi
fi
command -v java >/dev/null 2>&1 || die "Java not found. Install Temurin JDK 25: https://adoptium.net/"
JAVA_LINE="$(java -version 2>&1 | head -n 1)"
JAVA_MAJOR="$(printf '%s\n' "$JAVA_LINE" | sed -E 's/.*version "([0-9]+).*/\1/')"
[ "$JAVA_MAJOR" = "25" ] || die "Java 25 required, found: $JAVA_LINE
Install Temurin JDK 25: https://adoptium.net/"
printf '  Java OK: %s\n' "$JAVA_LINE"
case "$JAVA_LINE" in
  *25.0.2*) printf '  Note: 25.0.2 can break Spotless. Use 25.0.5+ for the verify script.\n' ;;
esac

# Maven
say "Checking Maven ..."
if ! command -v mvn >/dev/null 2>&1; then
  for candidate in /opt/homebrew/bin/mvn /usr/local/bin/mvn /opt/maven/bin/mvn \
                   /usr/share/maven/bin/mvn "$HOME"/.sdkman/candidates/maven/*/bin/mvn; do
    if [ -x "$candidate" ]; then
      export PATH="$(dirname "$candidate"):$PATH"
      break
    fi
  done
fi
command -v mvn >/dev/null 2>&1 || {
  if [ "$(uname -s)" = "Darwin" ]; then
    die "Maven not found. Install it with: brew install maven"
  else
    die "Maven not found. Install it with: sudo apt install maven (or dnf/pacman), or SDKMAN."
  fi
}
mvn -version >/dev/null 2>&1 || die "Maven cannot start. Run 'mvn -version' to see the error."
printf '  Maven OK: %s\n' "$(mvn -version 2>/dev/null | head -n 1)"

# Database config. Order: real env vars > .env > db.properties.
say "Checking database config ..."
DB_USER="${DB_USERNAME:-}"
DB_HOST="${DB_HOST:-}"
DB_PORT="${DB_PORT:-}"
DB_NAME="${DB_DATABASE:-}"
[ -n "$DB_USER" ] || DB_USER="$(read_cfg DB_USERNAME .env)"
[ -n "$DB_USER" ] || DB_USER="$(read_cfg db.user db.properties)"
[ -n "$DB_HOST" ] || DB_HOST="$(read_cfg DB_HOST .env)"
[ -n "$DB_HOST" ] || DB_HOST="$(read_cfg db.host db.properties)"
[ -n "$DB_PORT" ] || DB_PORT="$(read_cfg DB_PORT .env)"
[ -n "$DB_PORT" ] || DB_PORT="$(read_cfg db.port db.properties)"
[ -n "$DB_NAME" ] || DB_NAME="$(read_cfg DB_DATABASE .env)"
[ -n "$DB_NAME" ] || DB_NAME="$(read_cfg db.name db.properties)"
[ -n "$DB_USER" ] || die "Missing DB config. Copy .env.example to .env and set DB_USERNAME (or set db.user in db.properties)."
[ -n "$DB_HOST" ] || DB_HOST=localhost
[ -n "$DB_PORT" ] || DB_PORT=3306
[ -n "$DB_NAME" ] || DB_NAME=uhs_db
printf '  Target: %s@%s:%s/%s (password hidden)\n' "$DB_USER" "$DB_HOST" "$DB_PORT" "$DB_NAME"
[ -f .env ] || printf '  Note: .env not found; using environment variables or db.properties.\n'

case "$COMMAND" in
  check)  say "All checks passed." ;;
  start)  say "Starting the app ..."; mvn -q exec:java ;;
  verify) say "Full build (tests, format, style) ..."; mvn -B verify ;;
  build)  say "Quick build (no tests) ..."; mvn -q -DskipTests package ;;
  *) die "Unknown command: $COMMAND (use check, start, verify, or build)" ;;
esac
RC=$?
if [ "$RC" -eq 0 ]; then
  say "Done."
else
  printf '\n[UHS] Command failed (exit %s). See the output above.\n' "$RC" >&2
fi
exit "$RC"
