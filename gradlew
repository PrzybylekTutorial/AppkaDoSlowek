#!/usr/bin/env sh
# Minimal shim to use gradle if installed, else fail helpfully
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
else
  echo "Gradle wrapper is missing. Please install Gradle and run: gradle wrapper; then use ./gradlew" >&2
  exit 1
fi