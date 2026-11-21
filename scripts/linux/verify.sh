#!/usr/bin/env bash
# Full build: tests, format and style checks.
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec bash "$HERE/../unix/run.sh" verify
