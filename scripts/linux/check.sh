#!/usr/bin/env bash
# Check Java, Maven and the database config only.
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec bash "$HERE/../unix/run.sh" check
