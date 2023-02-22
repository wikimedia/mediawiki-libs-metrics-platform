#! /bin/bash
set -e

if [ -z "$1" ]
then
	echo "Version is missing! The new version number must be specified."
	echo "Usage: prepare-release.sh VERSION"
	echo "VERSION is passed to 'npm version'; it can be a version number, or a string like 'major' or 'minor'"
	exit 1
fi

# Build everything (and exit if that fails)
# NOTE: If we ever embed the version number in any of the build results,
# we will have to build after incrementing the version, then leave things half-baked
# if the build fails
npm run build

# Get the old version from package.json before running npm version
OLD_VERSION="$(node -p 'require("./package.json").version')"
# Update package.json
npm version --no-git-tag-version $1

# Unfortunately this doesn't update package-lock.json, update that manually
npm install
# Get the updated version from package.json
NEW_VERSION="$(node -p 'require("./package.json").version')"

# Get the commits in the new version and prepend them to CHANGELOG.md
NEW_COMMITS="$(git log --reverse tag-js-$OLD_VERSION.. --format="- %s (%aN)" . )"
TODAY="$(date -u +%Y-%m-%d)"
echo -e "# $NEW_VERSION / $TODAY\n$NEW_COMMITS\n\n$(cat CHANGELOG.md)" > CHANGELOG.md

npm publish --dry-run

# Checkout new commit branch
git checkout -b "tag-js-$NEW_VERSION"

# After manually sorting the changes according to code contribution guidelines,
# commit all changes with
# git commit -am "Tag v$NEW_VERSION"