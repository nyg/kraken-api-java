#!/usr/bin/env sh

CURRENT_DIR=$(basename $PWD)

if [ "$CURRENT_DIR" = "kraken-api-java" ]
then
  VERSION=$(grep scm.tag= release.properties | awk -Fv '{print $2}')
  echo Generating changelog for version $VERSION

  git cliff -o CHANGELOG.md --tag $VERSION
  git add CHANGELOG.md
fi
