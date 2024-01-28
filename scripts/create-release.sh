mvn -Dtag=v1.0.0 -DreleaseVersion=1.0.0 -DdevelopmentVersion=1.0.1-SNAPSHOT release:prepare release:perform
git fetch
