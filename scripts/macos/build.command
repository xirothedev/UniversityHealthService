#!/usr/bin/env bash
# Double-click in Finder for a quick build (no tests).
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec bash "$HERE/../unix/run.sh" build
