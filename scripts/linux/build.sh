#!/usr/bin/env bash
# Quick build, no tests.
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec bash "$HERE/../unix/run.sh" build
