#!/usr/bin/env bash
# Start the University Health Service app.
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec bash "$HERE/../unix/run.sh" start
