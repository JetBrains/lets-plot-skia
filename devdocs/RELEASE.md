## Releasing the Project

### Make Version

##### 1. Update Documentation

- Update CHANGELOG.md
- Update the "Dependencies" section in README.md

##### 2. Set release version

- remove _"-SNAPSHOT"_ qualifier (the 'version' property in the root 'build.gradle.kts').

##### 3. Build and release artifacts to Sonatype repository / Maven Central

- `./gradlew clean`
- `./gradlew build`
- `./gradlew publishAllPublicationsToSonatypeRepository`
- `./gradlew findSonatypeStagingRepository closeAndReleaseSonatypeStagingRepository`                   

> **Note**: For more details see [PUBLISHING.md](PUBLISHING.md).

##### 4. Prepare to the next dev cycle

- Increment the version and add _"-SNAPSHOT"_ qualifier (the 'version' property in the root 'build.gradle.kts')
- Push all to git and add the version git tag:
    - `git add --all && git commit -m "Release vX.X.X" && git push`
    - `git tag vX.X.X && git push --tags`

### Add the GitHub Release:
 
 * Open the link: https://github.com/JetBrains/lets-plot-skia/releases/new
 * Fill `Tag version` and `Release title` with the released version: "vX.X.X"
 * Fill the description field - copy from the CHANGELOG.md


### Update Dependant Projects 

- Update the version of the `lets-plot-skia` dependency in the [lets-plot-compose-demos](https://github.com/JetBrains/lets-plot-compose-demos) project.
