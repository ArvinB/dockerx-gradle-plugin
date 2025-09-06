/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*******************************************************************************/

package io.github.arvinb.dockerx;

/**
 * DockerXConstants for the DockerX Gradle Plugin
 */
public class DockerXConstants {
    
    // Static class members

    public static final String NEW_LINE                    = System.lineSeparator();
    public static final String EMPTY_STRING                = "";
    public static final String COMMA                       = ",";
    public static final String AUTHORIZATION               = "Authorization";
    public static final String BEARER                      = "Bearer";
    
    public static final String TASK_GROUP                  = "dockerx";
    
    public static final String NAMED_CONFIG                = "config";

    public static final String EXTENSION_NAME              = "dockerXSpecs";
    public static final String TASK_BUILD                  = "dockerXBuild";
    public static final String TASK_CATALOG                = "dockerXCatalog";
    public static final String TASK_DIGEST                 = "dockerXDigest";
    public static final String TASK_DOWNLOAD               = "dockerXDownload";
    public static final String TASK_EXECUTE                = "dockerXExecute";
    public static final String TASK_GITHUB                 = "dockerXGitHub";
    public static final String TASK_OPM_BUNDLE             = "dockerXOPMBundle";
    public static final String TASK_OPM_REGISTRY           = "dockerXOPMRegistry";
    public static final String TASK_OPM_VALIDATE           = "dockerXOPMValidate";
    public static final String TASK_SQLITE_EXECUTE         = "dockerXSQLiteExecute";
    public static final String TASK_OPM_RENDER             = "dockerXOPMRender";
    public static final String TASK_OPM_INIT               = "dockerXOPMInit";
    
    public static final String LABEL_DOCKERX               = "DockerX";
    public static final String LABEL_BASE                  = "Base";
    public static final String LABEL_GITHUB                = "GitHub";
    public static final String LABEL_DOWNLOAD              = "Download";
    public static final String LABEL_DOCKERX_BUILD         = "DockerX Build";
    public static final String LABEL_CATALOG               = "Catalog";
    public static final String LABEL_EXECUTE               = "Execute";
    public static final String LABEL_OPM                   = "OPM";
    public static final String LABEL_SKIP                  = "Skip";
    public static final String LABEL_SQLITE                = "SQLite";
    
    public static final Boolean DEFAULT_TRUE               = Boolean.TRUE;
    public static final Boolean DEFAULT_FALSE              = Boolean.FALSE;
    
    public static final String DEFAULT_DOCKER              = "docker";
    public static final String DEFAULT_OLM_PACKAGE         = "olm.package";
    public static final String DEFAULT_REPLACES            = "replaces";
    public static final String DEFAULT_YAML                = "yaml";
    
    public static final String DOCKERX_DIGEST              = "Digest";
    public static final String DOCKERX_PLATFORM            = "Platform";
    public static final String DOCKERX_DESCRIPTOR          = "Descriptor";
    
    public static final String BUNDLES_DB                  = "bundles.db";
    
    public static final String CATALOG_HEADER              = "========= BEGIN CATALOG SOURCE" + NEW_LINE;
    public static final String CATALOG_FOOTER              = "========= CATALOG SOURCE END" + NEW_LINE;
    public static final String CATALOG_ICON                = "icon.svg";
    public static final String CATALOG_README              = "README.md";
    public static final String CATALOG_SOURCE              = "apiVersion: operators.coreos.com/v1alpha1" + NEW_LINE
                                                           + "kind: CatalogSource" + NEW_LINE
                                                           + "metadata:" + NEW_LINE
                                                           + "  name: {0}" + NEW_LINE
                                                           + "  namespace: openshift-marketplace" + NEW_LINE
                                                           + "spec:" + NEW_LINE
                                                           + "  sourceType: grpc" + NEW_LINE
                                                           + "  displayName: {1}" + NEW_LINE
                                                           + "  image: {2}" + NEW_LINE;
    
    public static final String CMD_LINE_ADD                = "add";
    public static final String CMD_LINE_ALPHA              = "alpha";
    public static final String CMD_LINE_BUILD              = "build";
    public static final String CMD_LINE_BUILDX             = "buildx";
    public static final String CMD_LINE_BUNDLE             = "bundle";
    public static final String CMD_LINE_CREATE             = "create";
    public static final String CMD_LINE_DOCKER             = "docker";
    public static final String CMD_LINE_DOCKER_CONTAINER   = "docker-container";
    public static final String CMD_LINE_IMAGETOOLS         = "imagetools";
    public static final String CMD_LINE_INIT               = "init";
    public static final String CMD_LINE_INSPECT            = "inspect";
    public static final String CMD_LINE_LATEST             = "latest";
    public static final String CMD_LINE_LOGIN              = "login";
    public static final String CMD_LINE_MANIFEST           = "manifest";
    public static final String CMD_LINE_MANIFESTS          = "manifests";
    public static final String CMD_LINE_MULTIARCH          = "multiarch/qemu-user-static";
    public static final String CMD_LINE_OPM                = "opm";
    public static final String CMD_LINE_OPMVERSION         = "OpmVersion";
    public static final String CMD_LINE_PUSH               = "push";
    public static final String CMD_LINE_PRUNE              = "prune";
    public static final String CMD_LINE_REGISTRY           = "registry";
    public static final String CMD_LINE_RENDER             = "render";
    public static final String CMD_LINE_RUN                = "run";
    public static final String CMD_LINE_SQLITE             = "sqlite3";
    public static final String CMD_LINE_SYSTEM             = "system";
    public static final String CMD_LINE_USE                = "use";
    public static final String CMD_LINE_VALIDATE           = "validate";
    public static final String CMD_LINE_VERSION            = "version";
    public static final String CMD_LINE_VOLUME             = "volume";
    public static final String CMD_LINE_YES                = "yes";
    public static final String CMD_LINE_LS                 = "ls";
    public static final String CMD_LINE_RM                 = "rm";
    public static final String CMD_LINE_OPT_P              = "-p";
    public static final String CMD_LINE_OPT_U              = "-u";
    public static final String CMD_LINE_OPT_ALL            = "--all";
    public static final String CMD_LINE_OPT_ALL_TAGS       = "--all-tags";
    public static final String CMD_LINE_OPT_AMEND          = "--amend";
    public static final String CMD_LINE_OPT_BUILD_ARG      = "--build-arg";
    public static final String CMD_LINE_OPT_BUNDLE_IMAGES  = "--bundle-images";
    public static final String CMD_LINE_OPT_CHANNELS       = "--channels";
    public static final String CMD_LINE_OPT_CONTAINER_TOOL = "--container-tool";
    public static final String CMD_LINE_OPT_DEFAULT        = "--default";
    public static final String CMD_LINE_OPT_CHANNEL_DEF    = "--default-channel";
    public static final String CMD_LINE_OPT_DESCRIPTION    = "--description";
    public static final String CMD_LINE_OPT_DIRECTORY      = "--directory";
    public static final String CMD_LINE_OPT_FILE           = "--file";
    public static final String CMD_LINE_OPT_FORCE          = "--force";
    public static final String CMD_LINE_OPT_ICON           = "--icon";
    public static final String CMD_LINE_OPT_IMAGE_BUILDER  = "--image-builder";
    public static final String CMD_LINE_OPT_MODE           = "--mode";
    public static final String CMD_LINE_OPT_NAME           = "--name";
    public static final String CMD_LINE_OPT_LOAD           = "--load";
    public static final String CMD_LINE_OPT_OUTPUT         = "--output";
    public static final String CMD_LINE_OPT_PACKAGE        = "--package";
    public static final String CMD_LINE_OPT_PERSISTENT     = "--persistent";
    public static final String CMD_LINE_OPT_PLATFORM       = "--platform";
    public static final String CMD_LINE_OPT_PRIVILEGED     = "--privileged";
    public static final String CMD_LINE_OPT_PROVENANCE     = "--provenance";
    public static final String CMD_LINE_OPT_PURGE          = "--purge";
    public static final String CMD_LINE_OPT_PUSH           = "--push";
    public static final String CMD_LINE_OPT_RESET          = "--reset";
    public static final String CMD_LINE_OPT_RM             = "--rm";
    public static final String CMD_LINE_OPT_SECRET         = "--secret";
    public static final String CMD_LINE_OPT_TAG            = "--tag";
    public static final String CMD_LINE_OPT_TARGET         = "--target";
    public static final String CMD_LINE_OPT_VERBOSE        = "--verbose";
    public static final String CMD_LINE_OPT_VERSION        = "--version";

    public static final String  VERIFY_MIN_DOCKER_VER      = "19.03";
    public static final String  VERIFY_MIN_OPM_VER         = "1.15";
    public static final String  VERIFY_MIN_SQLITE_VER      = "3.26";
    public static final String  VERIFY_GITHUB_GIT          = "git@";
    public static final String  VERIFY_GITHUB_HTTP         = "http";
    
    /// Candidates for translation
    
    public static final String  VERIFY_DOCKER_RUNNING      = "Docker is required to be running.";
    public static final String  VERIFY_DOCKER_VERSION      = "Docker must be greater than or equal to 19.03+.";
    public static final String  VERIFY_GITHUB_REPO_URI     = "A valid GitHub URI is required.";
    public static final String  VERIFY_OPM_REQUIRED        = "Operator Package Manager is required.";
    public static final String  VERIFY_OPM_VERSION         = "Operator Package Manager must be greater than or equal to 1.15+.";
    public static final String  VERIFY_SQLITE_VERSION      = "SQLite must be greater than or equal to 3.26+.";
    
    public static final String  MESSAGE_GITHUB_CLONE           = "GitHub Clone Exception" + NEW_LINE;
    public static final String  MESSAGE_ACTION_EXEC            = "Action Execute Exception" + NEW_LINE;
    public static final String  MESSAGE_ACTION_ITEM            = "Action Item" + NEW_LINE;
    public static final String  MESSAGE_CMD_ACTIVATE_MULTIARCH = "Activating Multi-Arch" + NEW_LINE;
    public static final String  MESSAGE_CMD_BUILD_IMAGE        = "Building image" + NEW_LINE;
    public static final String  MESSAGE_CMD_CREATE_BUILDER     = "Creating a builder" + NEW_LINE;
    public static final String  MESSAGE_CMD_CREATE_MANIFEST    = "Creating list manifest" + NEW_LINE;
    public static final String  MESSAGE_CMD_GET_BUILDERS       = "Getting list of builders" + NEW_LINE;
    public static final String  MESSAGE_CMD_INSPECT_IMAGE      = "Inspecting image" + NEW_LINE;
    public static final String  MESSAGE_CMD_INSPECT_MANIFEST   = "Inspecting list manifest" + NEW_LINE;
    public static final String  MESSAGE_CMD_LOGIN_DOCKER_XAUTH = "Logging into registry" + NEW_LINE;
    public static final String  MESSAGE_CMD_PRUNE_IMAGES       = "Pruning images" + NEW_LINE;
    public static final String  MESSAGE_CMD_PRUNE_SYS_IMAGES   = "Pruning system images" + NEW_LINE;
    public static final String  MESSAGE_CMD_PRUNE_VOLUMES      = "Pruning volumes" + NEW_LINE;
    public static final String  MESSAGE_CMD_PUSH_IMAGE         = "Pushing image" + NEW_LINE;
    public static final String  MESSAGE_CMD_PUSH_MANIFEST      = "Pushing list manifest" + NEW_LINE;
    public static final String  MESSAGE_CMD_REMOVE_BUILDER     = "Removing a builder" + NEW_LINE;
    public static final String  MESSAGE_CMD_REMOVE_MANIFEST    = "Removing list manifest" + NEW_LINE;
    public static final String  MESSAGE_CMD_USE_BUILDER        = "Using a builder" + NEW_LINE;
    public static final String  MESSAGE_DOCKER_LOGIN_REG       = "Docker login: Registry({0}), Username({1})" + NEW_LINE;
    public static final String  MESSAGE_DOCKER_IMAGE_ID        = "Docker Client Image ID" + NEW_LINE;
    public static final String  MESSAGE_DOCKER_REGISTRY        = "Docker Registry not found" + NEW_LINE;
    public static final String  MESSAGE_DOCKER_BUILD           = "Docker Build" + NEW_LINE;
    public static final String  MESSAGE_DOWNLOAD_URL           = "Download URL Exception" + NEW_LINE;
    public static final String  MESSAGE_DOWNLOAD_EXEC          = "Download Execute Exception" + NEW_LINE;
    public static final String  MESSAGE_DIGEST                 = "Digest Exception" + NEW_LINE;
    public static final String  MESSAGE_DIGEST_SUBST           = "Digest Substitution: Digest({0}), File({1})" + NEW_LINE;
    public static final String  MESSAGE_DIGEST_EXEC            = "Digest Execute Exception" + NEW_LINE;
    public static final String  MESSAGE_DOCKER_BUILD_EXEC      = "Docker Build Execute Exception" + NEW_LINE;
    public static final String  MESSAGE_OPM_BUNDLE_BUILD       = "Operator Package Manager Bundle Build" + NEW_LINE;
    public static final String  MESSAGE_OPM_EXEC               = "Operator Package Manager Execute Exception" + NEW_LINE;
    public static final String  MESSAGE_OPM_INIT               = "Operator Package Manager Init OLM" + NEW_LINE;
    public static final String  MESSAGE_OPM_REGISTRY_ADD       = "Operator Package Manager Registry Add" + NEW_LINE;
    public static final String  MESSAGE_OPM_RENDER_BUNDLE      = "Operator Package Manager Bundle Render" + NEW_LINE;
    public static final String  MESSAGE_OPM_STORE_OUTPUT       = "Operator Package Manager Store Output" + NEW_LINE;
    public static final String  MESSAGE_OPM_VALIDATE           = "Operator Package Manager Validate" + NEW_LINE;
    public static final String  MESSAGE_OPM_VERSION_LIST       = "Operator Package Manager Version List Exception" + NEW_LINE;
    public static final String  MESSAGE_SQLITE_EXEC            = "SQLite Execute Exception" + NEW_LINE;
    public static final String  MESSAGE_SQLITE_STMT_EXEC       = "SQLite Execute Statement Exception" + NEW_LINE;
    public static final String  MESSAGE_VERSION_COMPARISON     = "Version Comparison Exception" + NEW_LINE;
    
    ///
}
