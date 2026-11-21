#!/usr/bin/env bash
# Double-click in Finder for the full build (tests, format, style).
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec bash "$HERE/../unix/run.sh" verify
