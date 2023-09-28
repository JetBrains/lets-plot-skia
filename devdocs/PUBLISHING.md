### Publishing to local Maven repository

> **Note**: Our custom local Maven repository is located at `<project root>/.maven-publish-dev-repo`.

`./gradlew publishAllPublicationsToMavenLocalRepository`


### Publishing to Sonatype Maven repository

> **Note**: When publishing to Sonatype, PGP signature is required.
>
> See: https://central.sonatype.org/pages/working-with-pgp-signatures.html
        
                                                               
#### Credentials
                                 
In the `local.properties` file add the following properties:
```properties
sonatype.username=<your Sonatype username>
sonatype.password=<your Sonatype password>
```

#### SNAPSHOT version

Specify "x.y.z-SNAPSHOT" version in `build.gradle.kts` file.

`./gradlew publishAllPublicationsToSonatypeRepository`

> You can find published SNAPSHOT artifacts here https://oss.sonatype.org/index.html#view-repositories;snapshots~browsestorage \
> In the "Browse Storage" tab enter ‘Path lookup’: org/jetbrains/lets-plot


#### Release version

  a) Specify RELEASE or PRE-RELEASE version in `build.gradle.kts` file.

  b) Upload to the Nexus staging repository:

`./gradlew publishAllPublicationsToSonatypeRepository`

> **Note**: Publish tasks should be invoked with a single command to avoid splitting the staging repository.
>
> Check artifacts are uploaded to staging repository:
>
> https://oss.sonatype.org/index.html#stagingRepositories
>
> Should see repository: "orgjetbrainslets-plot-NNNN" (where NNNN is a number)
> with profile: "org.jetbrains.lets-plot".

  c) Publish all artifacts to "Releases" repository (from the staging):

`./gradlew findSonatypeStagingRepository closeAndReleaseSonatypeStagingRepository`

> Check artifacts are uploaded to Nexus Releases repository:
>
> https://oss.sonatype.org/index.html#view-repositories;releases~browsestorage
>
> In the "Browse Storage" tab enter ‘Path lookup’: org/jetbrains/lets-plot
