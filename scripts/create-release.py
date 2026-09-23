#!/usr/bin/env python3
import subprocess
import sys
import webbrowser
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
API_KEYS = ROOT / "examples/src/main/resources/api-keys.properties"
PUBLISHING_URL = "https://central.sonatype.com/publishing"
BUMPS = ("patch", "minor", "major")


def latest_version():
    tags = subprocess.run(
        ["git", "tag", "--list", "v[0-9]*", "--sort=-v:refname"],
        cwd=ROOT, check=True, capture_output=True, text=True,
    ).stdout.split()
    return tags[0].removeprefix("v")


def bump(version, part):
    major, minor, patch = map(int, version.split("."))
    if part == "major":
        return f"{major + 1}.0.0"
    if part == "minor":
        return f"{major}.{minor + 1}.0"
    return f"{major}.{minor}.{patch + 1}"


def main():
    if len(sys.argv) != 2 or sys.argv[1] not in BUMPS:
        sys.exit(f"usage: {Path(sys.argv[0]).name} {'|'.join(BUMPS)}")

    if API_KEYS.exists():
        sys.exit(f"abort: remove {API_KEYS} before releasing")

    current = latest_version()
    release = bump(current, sys.argv[1])
    development = f"{bump(release, 'patch')}-SNAPSHOT"

    if input(f"Release {current} -> {release}, next development version {development}? [y/N] ").lower() != "y":
        sys.exit("aborted")

    maven = subprocess.run([
        "mvnd", "--serial",
        f"-Dtag=v{release}",
        f"-DreleaseVersion={release}",
        f"-DdevelopmentVersion={development}",
        "release:prepare", "release:perform",
    ], cwd=ROOT)
    if maven.returncode:
        sys.exit("release failed")

    if input(f"Open {PUBLISHING_URL} to publish {release}? [Y/n] ").lower() != "n":
        webbrowser.open(PUBLISHING_URL)


if __name__ == "__main__":
    main()
