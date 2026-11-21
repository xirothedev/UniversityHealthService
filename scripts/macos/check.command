#!/usr/bin/env bash
# Double-click in Finder to check Java, Maven and the database config only.
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec bash "$HERE/../unix/run.sh" check
