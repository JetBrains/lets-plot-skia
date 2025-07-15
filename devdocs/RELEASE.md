## Releasing the Project

### Make Version

##### 1. Update Documentation

- Update CHANGELOG.md
- Update the "Dependencies" section in README.md

##### 2. Set the release version

- remove _"-SNAPSHOT"_ qualifier (the 'version' property in the root 'build.gradle.kts').

##### 3. Build and release artifacts to Sonatype Central repository

Make sure that JNI libraries in the `skiko-jni-libs` directory are up to date and 
match the version of the `Skiko` library used in the `Compose Multiplatform`. Refer [DEVELOPMENT.md](DEVELOPMENT.md) for details.

- `./gradlew clean`
- `./gradlew build`
- `./gradlew packageSkikoJniLibs`
- `./gradlew publishAllPublicationsToMavenRepository`
- `./gradlew uploadMavenArtifacts`

Go to the Sonatype Central Repository deployments page:

https://central.sonatype.com/publishing/deployments

Check all artifacts were uploaded and validated, then push the "Publish" button.

> **Note**: For more details see [PUBLISHING.md](PUBLISHING.md).

##### 4. Prepare for the next dev cycle

- Increment the version and add _"-SNAPSHOT"_ qualifier (the 'version' property in the root 'build.gradle.kts')
- Push all to git and add the version git tag:
    - `git add --all && git commit -m "Release vX.X.X" && git push`
    - `git tag vX.X.X && git push --tags`

### Add the GitHub Release:
 
 * Open the link: https://github.com/JetBrains/lets-plot-skia/releases/new
 * Fill `Tag version` and `Release title` with the released version: "vX.X.X"
 * Fill the description field - copy from the CHANGELOG.md
 * **Attach the artifacts:**
   - `skiko-jni-libs.zip` from the project root


### Update Dependant Projects 

- Update the version of the `lets-plot-skia` dependency in the [lets-plot-compose-demos](https://github.com/JetBrains/lets-plot-compose-demos) project.
