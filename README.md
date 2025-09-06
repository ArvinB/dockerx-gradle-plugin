# DockerX Gradle Plugin
  
Publishing to gradle  
  
```
gradle build publishPlugins --validate-only
gradle build publishPlugins
```
  
---
This is a Docker Buildx plugin for [Gradle](http://www.gradle.org/). This is a Java-based plugin that makes use of the CLI `docker buildx` to build multi-architecture images and a list manifest.  
  
## Apply Plugin Configuration

1. Apply the plugin in your `build.gradle` file:

    ```groovy
    plugins {
        id 'io.github.arvinb.dockerx' version 'latest.release'
    }
    ```

## Usage

After you have applied the plugin configuration (see above), you can use the following tasks:

- [`dockerXBuild`](#dockerxbuild): Performs a set of Docker Buildx commands based on the properties.
- [`dockerXCatalog`](#dockerxcatalog): Generates the catalog source based on the build of an index image.
- [`dockerXDigest`](#dockerxdigest): Modify an image to use its digest in a file during a build runtime.
- [`dockerXDownload`](#dockerxdownload): Downloads artifacts into build folder for use during a build runtime.
- [`dockerXExecute`](#dockerxexecute): Execute a command line action for use during a build runtime.
- [`dockerXGitHub`](#dockerxgithub): Clones a GitHub repository for use during a build runtime.
- [`dockerXOPMBundle`](#dockerxopmbundle): Executes an `opm bundle...` command.
- [`dockerXOPMInit`](#dockerxopminit): Executes an `opm init...` command.
- [`dockerXOPMRegistry`](#dockerxopmregistry): Executes an `opm registry...` command.
- [`dockerXOPMRender`](#dockerxopmrender): Executes an `opm render...` command.
- [`dockerXOPMValidate`](#dockerxopmvalidate): Executes an `opm validate...` command.
- [`dockerXSQLiteExecute`](#dockerxsqliteexecute): Executes SQLite db commands against an SQLite db such as a bundle.

Building a Docker image is performed after cloning a GitHub repository. Thus, it is advised to perform a `delete layout.buildDirectory` as part of the default `clean` task and set this task as a dependency. Also, GitHub SSH keys are used if credentials are not supplied.  

```groovy
clean {
  delete layout.buildDirectory
}

task dockerBuild(type: dockerXBuild) {

    dependsOn 'clean'

    dockerXSpecs {
    
      dockerSpecs.dryrun 'false'.toBoolean()
      dockerSpecs.images ['docker.io/DOCKER_IMAGE:1.0.0', 'docker.io/DOCKER_IMAGE:latest']
      dockerSpecs.platforms ['linux/amd64', 'linux/arm64']
    }
}
```
  
Alternative to defining a formal task, you may also use it with Groovy domain-specific language (DSL) in your build script.  
  
```groovy
dockerXBuild {

    dockerXSpecs {
    
      dockerSpecs.dryrun = 'false'.toBoolean()
      dockerSpecs.images = ['docker.io/DOCKER_IMAGE:1.0.0', 'docker.io/DOCKER_IMAGE:latest']
      dockerSpecs.platforms = ['linux/amd64', 'linux/arm64']
    }
}
```

## Prerequisites

- Gradle 9.0.0 or greater (required)
- Java 21
- Docker 19.03 or greater
- Operator Package Manager 1.15 or greater
- SQLite 3.26 or greater

**Note**: Each task has its own set of requirements. For example, the `dockerXBuild` task may require Docker (running daemon) while other tasks may not.
  
## Examples

### Build an image with a highly customized build process

```groovy

task dockerGitHub(type: io.github.arvinb.dockerx.task.DockerXGitHub) {
    
    dockerXSpecs {
    
      dockerSpecs.dryrun = 'false'.toBoolean() // Disable dryrun flag
      gitHubSpecs.gitHubRepoURI = 'git@github.com:myOrganization/myRepository' // GitHub repository
      gitHubSpecs.gitHubRepoBranch = 'myWorkingBranch' // GitHub branch for the given repository
      gitHubCreds.username      = 'myGitHubUsername'
      gitHubCreds.password      = 'myGitHubPassword'
    }
}

task dockerBuild(type: dockerXBuild) {
    
    dependsOn 'dockerGitHub'
    
    dockerXSpecs {
    
       dockerSpecs.dryrun = 'false'.toBoolean() // Disable dryrun flag
       dockerSpecs.images = ['myRegistry.io/myImagePath/myImage:1.0.0'] // Image with a single tag to build
       dockerSpecs.platforms = ['linux/amd64'] // Single platform to build on
       dockerSpecs.push = 'true'.toBoolean() // Push the image to a remote registry
       dockerSpecs.buildArgs = ['KeyArg1':'ValueArg1', 'KeyArg2':'ValueArg2'] // Build arguments to pass at build time
       dockerSpecs.secrets = ['myDockerSecretId':'/tmp/myDockerSecret'] // Docker secrets to pass at build time
       dockerSpecs.target = 'myTargetBuild' // Target build stage
       dockerSpecs.workingDir = 'myBuildDir' // Working directory for the given repository
       dockerSpecs.dockerfile = 'dockerfiles/myDockerfile' // Dockerfile to use relative to the working direoctry
       
       // Digest to acquire of a different image and two files to replace the image tag with its digest sha
       dockerSpecs.digestSpecs = ['myRegistry.io/someImagePath/otherImage:0.0.1':['path/to/file1', 'path/to/file2']]
       
       // Docker remote registry credentials
       dockerCreds.username = 'myDockerRegistryUsername'
       dockerCreds.password = 'myDockerRegistryPassword'
    }
}
```

### Build a multi-architecture image with downloaded artifacts

```groovy

task dockerGitHub(type: io.github.arvinb.dockerx.task.DockerXGitHub) {
    
    dockerXSpecs {
    
      dockerSpecs.dryrun = 'false'.toBoolean() // Disable dryrun flag
      gitHubSpecs.gitHubRepoURI = 'git@github.com:myOrganization/myRepository' // GitHub repository
      gitHubSpecs.gitHubRepoBranch = 'myWorkingBranch' // GitHub branch for the given repository
      gitHubCreds.username      = 'myGitHubUsername'
      gitHubCreds.password      = 'myGitHubPassword'
    }
}

task dockerDownload(type: io.github.arvinb.dockerx.task.DockerXDownload) {
    
    dependsOn 'dockerGitHub'
    
    dockerXSpecs {
       
       dockerSpecs.dryrun = 'false'.toBoolean() // Disable dryrun flag
       
       // Target artifact to download and the site to download the artifact from
       dockerSpecs.downloads = ['relative/path/myArtifact.txt':'https://download.com/my/path/artifactABC.txt']
       
       // Download remote artifact credentials
       downloadCreds.username = 'myDownloadUsername'
       downloadCreds.password = 'myDownloadPassword'
    }
}

task dockerBuild(type: io.github.arvinb.dockerx.task.DockerXBuild) {
    
    dependsOn 'dockerDownload'
    
    dockerXSpecs {
    
       dockerSpecs.dryrun = 'false'.toBoolean() // Disable dryrun flag
       dockerSpecs.images = ['myRegistry.io/A/image:1.0.0','myRegistry.io/A/image:latest'] // Images to build
       dockerSpecs.platforms = ['linux/amd64', 'linux/s390x'] // Multi-architecture platforms to build on
       dockerSpecs.push = 'true'.toBoolean() // Push the images and list manifest to a remote registry
       dockerSpecs.overwrite = 'true'.toBoolean() // Overwrite the downloaded artifact if it already exists
       
       // Docker remote registry credentials
       dockerCreds.username = 'myDockerRegistryUsername'
       dockerCreds.password = 'myDockerRegistryPassword'
    }
}
```
  
### Build an image and modify the digest at build time

```groovy

task dockerGitHub(type: io.github.arvinb.dockerx.task.DockerXGitHub) {
    
    dockerXSpecs {
    
      dockerSpecs.dryrun = 'false'.toBoolean() // Disable dryrun flag
      gitHubSpecs.gitHubRepoURI = 'git@github.com:myOrganization/myRepository' // GitHub repository
      gitHubSpecs.gitHubRepoBranch = 'myWorkingBranch' // GitHub branch for the given repository
      gitHubCreds.username = 'myGitHubUsername'
      gitHubCreds.password = 'myGitHubPassword'
    }
}

task dockerDigest(type: io.github.arvinb.dockerx.task.DockerXDigest) {

    dependsOn 'dockerGithub'
    
    dockerXSpecs {
       
       // Acquires the digest sha of a pre-built image, then substitutes that value in the target files
       digestSpecs.digests = ['myRegistry.io/namespace/otherImage:1.0.0':['path/to/file1', 'path/to/file2']]
    }
}

task dockerExecute(type: io.github.arvinb.dockerx.task.DockerXExecute) {
    
    dependsOn 'dockerDigest'
    
    dockerXSpecs {
       
       // Execute a script or command with arguments
       execSpecs.actions = ['path/to/script.sh' : 'param1 param2']
    }
}

task dockerBuild(type: io.github.arvinb.dockerx.task.DockerXBuild) {
    
    dependsOn 'dockerExecute'
    
    dockerXSpecs {
    
       dockerSpecs.dryrun = 'false'.toBoolean() // Disable dryrun flag
       dockerSpecs.images = ['myRegistry.io/A/image:1.0.0','myRegistry.io/A/image:latest'] // Images to build
       dockerSpecs.platforms = ['linux/amd64', 'linux/s390x'] // Multi-architecture platforms to build on
       dockerSpecs.push = 'true'.toBoolean() // Push the images and list manifest to a remote registry
       dockerSpecs.overwrite = 'true'.toBoolean() // Overwrite the downloaded artifact if it already exists
       
       // Docker remote registry credentials
       dockerCreds.username = 'myDockerRegistryUsername'
       dockerCreds.password = 'myDockerRegistryPassword'
    }
}
```

### Print and generate a catalog source YAML from an OLM image

```groovy
task dockerCatalog(type: io.github.arvinb.dockerx.task.DockerXCatalog) {
    
    dockerXSpecs {
    
      dockerSpecs.dryrun = 'false'.toBoolean() // Disable dryrun flag
      catalogSpecs.metadataName = 'my-operator-catalog' // Metadata name of Catalog Source
      catalogSpecs.displayName = 'My Operator Catalog'  // Display name of Catalog Source
      catalogSpecs.image = 'myRegistry.io/myImagePath/myOpImage:1.0.0' // OLM Image
      
      // Docker remote registry credentials
      dockerCreds.username = 'myDockerRegistryUsername'
      dockerCreds.password = 'myDockerRegistryPassword'
    
    }
    
    doLast {
        def catalog_source = getCatalogSource()
        println catalog_source
        // ...Do more stuff here
    }
}
```
  
## Tasks

This reference section provides a concise description and lists supported properties for each task available through this DockerX plugin.

This DockerX plugin uses the `dockerXSpecs` extension, which allows properties to be set and used during the runtime execution of its tasks. The dockerXSpecs extension is comprised of multiple other interfaces as listed here:

- baseSpecs (DockerXBaseExt) - Base specs
- catalogSpec (DockerXCatalogExt) - Catalog specs
- digestSpecs (DockerXDigestExt) - Digest specs
- dockerSpecs (DockerXDockerExt) - Docker specs
- downloadSpecs (DockerXDownloadExt) - Download specs
- gitHubSpecs (DockerXGitHubExt) - GitHub specs
- opmSpecs (DockerXOPMExt) - OPM specs
- sqliteSpecs (DockerXSQLiteExt) - SQLite specs
- dockerCreds (DockerXCredentialExt) - Credential specs
- downloadCreds (DockerXCredentialExt) - Credential specs
- gitHubCreds (DockerXCredentialExt) - Credential specs

### dockerXBuild

This task builds a Docker image.  
This task supports the following properties:

- dockerXSpecs.baseSpecs
- dockerXSpecs.dockerSpecs
- dockerXSpecs.dockerCreds
  
### dockerXCatalog

This task will log and provide a catalog source YAML used to deploy an image into OpenShift using the Operator Lifecycle Manager ([OLM](https://operator-framework.github.io/olm-book/)) framework.  
This task supports the following properties:

- dockerXSpecs.baseSpecs
- dockerXSpecs.catalogSpecs
- dockerXSpecs.dockerCreds

### dockerXDigest

This task modifies images to use its digest in artifacts.  
This task supports the following properties:

- dockerXSpecs.baseSpecs
- dockerXSpecs.digestSpecs

### dockerXDownload

This task downloads artifacts.  
This task supports the following properties:

- dockerXSpecs.baseSpecs
- dockerXSpecs.downloadSpecs
- dockerXSpecs.downloadCreds

### dockerXExecute

This task executes actions.  
This task supports the following properties:

- dockerXSpecs.baseSpecs
- dockerXSpecs.execSpecs

### dockerXGitHub

This task clones a GitHub repository.  
This task supports the following properties:

- dockerXSpecs.baseSpecs
- dockerXSpecs.gitHubSpecs
- dockerXSpecs.gitHubCreds

### dockerXOPMBundle

This task executes an `opm bundle` against a package bundle.  
This task supports the following properties:

- dockerXSpecs.baseSpecs
- dockerXSpecs.dockerSpecs
- dockerXSpecs.opmSpecs
- dockerXSpecs.dockerCreds

### dockerXOPMInit

This task executes an `opm init` against a package bundle.  
This task utilizes the following properties:

- dockerXSpecs.baseSpecs
- dockerXSpecs.opmSpecs

### dockerXOPMRegistry

This task executes an `opm registry` against a package bundle.  
This task supports the following properties:

- dockerXSpecs.baseSpecs
- dockerXSpecs.dockerSpecs
- dockerXSpecs.opmSpecs
- dockerXSpecs.dockerCreds

### dockerXOPMRender

This task executes an `opm render` against a package bundle.  
This task utilizes the following properties:

- dockerXSpecs.baseSpecs
- dockerXSpecs.opmSpecs
- dockerXSpecs.dockerCreds

### dockerXOPMValidate

This task executes an `opm validate` against a package bundle.  
This task utilizes the following properties:

- dockerXSpecs.baseSpecs
- dockerXSpecs.dockerSpecs
- dockerXSpecs.opmSpecs
- dockerXSpecs.dockerCreds

### dockerXSQLiteExecute

This task executes SQLite database statements against an SQLite datasource.  
This task utilizes the following properties:

- dockerXSpecs.baseSpecs
- dockerXSpecs.sqliteSpecs
