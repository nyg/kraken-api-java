# Release process

The release process for kraken-api-java is based on four components: the maven-release-plugin, a GitHub Actions workflow, and the central-publishing-maven-plugin by Sonatype.

## Overview

When wanting to make a release, the following must/will be performed:

1. Manually execute the prepare goal of the maven-release-plugin, this will create a git tag and push it to the remote repository.
2. Once the tag is pushed, the GitHub Actions workflow will run and create a GitHub release.
3. Manually execute the perform goal of the maven-release-plugin, this will build and upload the artifacts to Sonatype's Central Repository.
4. Manually publish the uploaded artifacts via Sonatype's website.

In theory, all of these steps could be merge into a single GitHub Actions workflow. However, for the time being, we prefer to define a minimum amount of steps on GitHub. Regarding step 2, it could be run manually from one's own machine as it consists of a single GitHub REST API call.

Also note that we prefered using Sonatype's Central Repository than GitHub's own Maven repository.

### Documentation

* https://maven.apache.org/maven-release/maven-release-plugin/plugin-info.html
* https://docs.github.com/en/actions/learn-github-actions/understanding-github-actions
* https://central.sonatype.org/publish/publish-portal-maven/
* https://git-cliff.org/docs/

## Detailed steps

The Maven release plugin helps automatizing certain tasks when making releases for Maven-based projects. Releasing requires invoking two of the plugin's goals: `prepare` and `perform`.

In short, the `prepare` goal updates version numbers and creates commits and tag in the SCM (e.g. Git). The `perform` goal checks out the created tag, builds the project artifacts and publishes them on a remote Maven repository (depending, of course, on user configuration).

### `release:prepare`

The detailed list of steps executed by the prepare goal are defined [here](https://maven.apache.org/maven-release/maven-release-plugin/usage/prepare-release.html) and [here](https://maven.apache.org/maven-release/maven-release-manager/#prepare). In scope of this project, the main ones are:

1. Change the version numbers in the POM files. How the plugin will compute the version number to use for release (and the one to use for the next development) is defined by the `projectVersionPolicyId` and `projectVersionPolicyConfig`  goal parameters. As this mecanism does not allow us to specify whether we are performing a patch, minor or major release (if we are even using semantic versioning), we just pass these values via the `releaseVersion`, `developmentVersion` parameters (e.g. `-DreleaseVersion=2.0.0 -DdevelopmentVersion=2.0.1-SNAPSHOT` on the command line).

   Note: in multi-module projects, set `autoVersionSubmodules` to true (see main POM file).

2. Run the preparation goals. By default, the goals bound to the `clean` and `verify` lifecycle phases are executed. In this project we add a third goal: `exec:exec` (of MojoHaus' exec-maven-plugin), in order to execute the `scripts/generate-changelog.sh` script which will execute git-cliff to generate the CHANGELOG.md file. The config for git-cliff is in the `cliff.toml` file. The preparation goals are setup in the main POM file with the `preparationGoals` parameter.

   Note: these goals are run for every Maven module, which require us to do some trickery in the generate-changelog.sh script to only run in the parent module. It's also possible to specify a list of profiles to enable via the `preparationProfiles` parameter.

3. Create and push a commit with the changes made in the two steps above. The default commit message is something like "[maven-release-plugin] prepare release v2.0.0". The message can be customized with the following parameters: `scmCommentPrefix` and `scmReleaseCommitComment`. We only modify the prefix to `chore(release): ` in order to follow [conventional commits](https://www.conventionalcommits.org/).

   Note: both commits and tags are signed (this requires some Git config, GPG keys and the `signTag` goal parameter). It is possible to prevent pushing the commit to the remote repository by setting `pushChanges` to false.

4. Tag the commit created in step 3 and push the tag to the remote repository. We specify the tag name on the command line, using the `tag` parameter, e.g. `-Dtag=v2.0.0`.

5. Change the version numbers in the POM files to prepare for the next development version. As mentioned in the first step, this value is passed via the command line, with the `developmentVersion` parameter.

   Note: if some custom goal needs to be run after this change and before the commit, it can be specified via the `completionGoals` parameter.

6. Create and push a commit with the changes made above. The message of the commit can be customized with the `scmDevelopmentCommitComment`.

The prepare goal also creates a `release.properties` file that is used by the `perform` goal in order to know which tag to checkout and build.

In the end, when wanting to make a release, the command line looks like this:

```sh
mvn -Dtag=v2.0.0 -DreleaseVersion=2.0.0 -DdevelopmentVersion=2.0.1-SNAPSHOT release:prepare
```

If we want to make sure thing are correct before committing we can use `-DdryRun=true`. If we prefer not to push, we can use `-DpushChanges=false`. If the goal execution fails, or after a dry-run, we can clean created files using:

```sh
mvn --serial release:clean
```

### GitHub Actions workflow

As described above, a tag is created and pushed to the remote repository. In our case, that's GitHub. GitHub allows setting up workflows to run automatically when certain events are trigger. In `.github/workflows/github-release.yml` we define a workflow to run whenever a new tag is pushed. This workflow will execute the following steps:

1. Checkout the repository using the [checkout](https://github.com/actions/checkout) action.
2. Generate a CHANGELOG.md file using the [git-cliff-action](https://github.com/orhun/git-cliff-action).
3. Create a [GitHub release](https://github.com/nyg/kraken-api-java/releases) using the [GitHub CLI](https://cli.github.com/manual/gh_release_create) and give it the CHANGELOG.md file generated above to be used in the description.

### `release:perform`

The `perform` goal is simpler than the `prepare` goal. Its documentation can be found [here](https://maven.apache.org/maven-release/maven-release-plugin/usage/perform-release.html). In scope of this project, the two steps are:

1. Checkout out the version of the project specified in the `release.properties` file created by the `prepare` goal. This is done in the target folder.

2. Run the perform goals defined by the `goals` parameter. The default is `deploy site-deploy`. While this is left unchanged, we set the `releaseProfiles` to `sonatype-release`. In this user-defined profile we do the following:

   1. use the maven-source-plugin to create a source JAR (bound to the package phase),

   2. use the maven-javadoc-plugin to create a Javadoc JAR (bound to the package phase),

   3. use the maven-gpg-plugin to sign the JARs with a previously created GPG key (bound to the verify phase),

      Note: these steps are required in order to produce a deployment considered valid by Sonatype, see [here](https://central.sonatype.org/publish/requirements/).

   4. use the central-publishing-maven-plugin to upload the built artifacts to the Sonatype Central Repository (bound to the deploy phase). Once the artifacts are uploaded, they must be published. This can either be done manually through Sonatype's website, or by asking the plugin to do it for us by setting the `autoPublish` parameter to true. Also, as we don't want to publish the examples module, we exclude it with the `excludeArtifacts` parameter.

3. Remove the files left by `prepare` goal.

## Summary

```sh
# dry run
mvn --serial -Dtag=v2.0.0 -DreleaseVersion=2.0.0 -DdevelopmentVersion=2.0.1-SNAPSHOT -DdryRun release:prepare

# prepare release
mvn --serial -Dtag=v2.0.0 -DreleaseVersion=2.0.0 -DdevelopmentVersion=2.0.1-SNAPSHOT release:prepare

# zsh gitstatusd bug?
git fetch

# stay safe even if the examples module is not published
rm examples/src/main/resources/api-keys.properties

# perform release
mvn --serial clean release:perform
```
