#!/usr/bin/env bash
# Register clickable launchers in the desktop applications menu.
# Run this once after you clone or move the project folder.
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$HERE/../.." && pwd)"
DEST="${XDG_DATA_HOME:-$HOME/.local/share}/applications"
mkdir -p "$DEST"
chmod +x "$HERE"/*.sh "$HERE/../unix/run.sh"

make_launcher() {
  local id="$1" name="$2" script="$3" comment="$4" file="$DEST/uhs-$1.desktop"
  cat > "$file" <<EOF
[Desktop Entry]
Type=Application
Name=UHS - $name
Comment=$comment
Exec="$ROOT/scripts/linux/$script"
Terminal=true
Icon=utilities-terminal
Categories=Development;
EOF
  chmod +x "$file"
  printf 'Created %s\n' "$file"
}

make_launcher start "Start App" start.sh "Start the University Health Service app"
make_launcher build "Quick Build" build.sh "Build without tests"
make_launcher verify "Full Build" verify.sh "Run tests, format and style checks"
make_launcher check "Check Setup" check.sh "Check Java, Maven and the database config"

printf '\nOpen your applications menu and search for "UHS" to click a launcher.\n'
