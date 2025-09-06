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

package io.github.arvinb.dockerx.config;

import org.gradle.api.provider.*;
import org.gradle.api.tasks.*;

public interface DockerXOPMConfig {

    /**
     * A clean flag (Default: true)
     */
    @Input @Optional
    public Property<Boolean> getClean();

    /**
     * A push flag (Default: false)
     */
    @Input @Optional
    public Property<Boolean> getPush();

    /**
     * A overwrite metadata flag (Default: false)
     * To overwrite annotations.yaml locally if existed. By default, overwrite is set to false.
     */
    @Input @Optional
    public Property<Boolean> getOverwriteMetadata();

    /**
     * A default channel
     * The default channel for the bundle image
     */
    @Input @Optional
    public Property<String> getDefaultChannel();

    /**
     * A package name
     * The name of the package that bundle image belongs to (Required if directory is not pointing to a bundle in the nested bundle format)
     */
    @Input @Optional
    public Property<String> getPackageName();

    /**
     * The tool used for images/containers (Default: docker)
     * A tool to interact with container images (save, build, etc.). One of: [docker, podman]
     */
    @Input @Optional
    public Property<String> getTool();

    /**
     * A graph update mode (Default: replaces)
     * A graph update mode that defines how channel graphs are updated. One of: [replaces, semver, semver-skippatch]
     */
    @Input @Optional
    public Property<String> getMode();

    /**
     * The bundle directory for the manifests and metadata package (Overloaded property)
     * A relative path to the derived working directory
     * The directory where bundle manifests and metadata for a specific version are located
     */
    @Input @Optional
    public Property<String> getBundleDir();

    /**
     * List of channels
     * The list of channels that bundle image belongs to (Required if directory is not pointing to a bundle in the nested bundle format)
     */
    @Input @Optional
    public ListProperty<String> getChannels();

    /**
     * OPM Action to perform
     * Valid Options: build, add
     */
    @Input @Optional
    public Property<String> getAction();

    /**
     * A list of bundle images
     * A comma separated list of links to bundle image
     * OPM Build: The image tag applied to the bundle image (first item in the list used)
     */
    @Input @Optional
    public ListProperty<String> getBundleImages();

    /**
     * A list of bundle sources, images, files or db
     * A comma separated list of links to bundle sources
     */
    @Input @Optional
    public ListProperty<String> getBundleSources();

    /**
     * Output format (Default: yaml)
     * Output format to render bundle image, valid options: yaml, json
     */
    @Input @Optional
    public Property<String> getOutputFormat();

    /**
     * Catalog Icon .svg (Default: icon.svg)
     * A relative path to the derived working directory
     */
    @Input @Optional
    public Property<String> getCatalogIcon();

    /**
     * Catalog README.md (Default: README.md)
     * A relative path to the derived working directory
     */
    @Input @Optional
    public Property<String> getCatalogReadme();

    /**
     * OPM output file prefix
     * Prefix of the file with the bundle image tag
     */
    @Input @Optional
    public Property<String> getOutputPrefix();

}
