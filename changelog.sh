#!/usr/bin/env sh
CURRENT_DIR=$(basename $PWD)
if [ "$CURRENT_DIR" = "kraken-api-java" ]
then
  echo Generating changelog in CHANGELOG.md
  git cliff -o CHANGELOG.md
fi
