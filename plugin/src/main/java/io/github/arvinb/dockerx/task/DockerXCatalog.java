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

package io.github.arvinb.dockerx.task;

import org.gradle.api.tasks.*;
import org.gradle.api.*;
import org.gradle.api.file.*;
import io.github.arvinb.dockerx.*;
import io.github.arvinb.dockerx.util.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXCatalog extends DefaultTask {
    
    private String catalogSource = EMPTY_STRING + NEW_LINE;
    
    @Input @Optional
    public String getCatalogSource() {
        return catalogSource;
    }
    
    @TaskAction
    public void catalogTaskAction() {
        
        final DockerXExtension ext = getPluginExtension();
        getProject().getLogger().lifecycle(this.toString(ext));
        
        catalogSource =
        DockerXCatalogUtil.getCatalogSourceData(getProject(), 
                                                ext.getDryrun().get(), 
                                                ext.getCatalogConfig().getMetadataName().getOrElse(EMPTY_STRING), 
                                                ext.getCatalogConfig().getDisplayName().getOrElse(EMPTY_STRING), 
                                                ext.getCatalogConfig().getImage().getOrElse(EMPTY_STRING), 
                                                ext.getRegistryCredentials(ext.getDockerCredConfig()).get());
        
        getProject().getLogger().lifecycle(CATALOG_HEADER + catalogSource + CATALOG_FOOTER);

        if ((ext.getCatalogConfig().getCommitMessage().getOrNull() != null) && 
            (ext.getCatalogConfig().getCommitFile().getOrNull()    != null) &&
            (!ext.getDryrun().get())) {
            
            org.ajoberstar.grgit.Credentials gitHubCredentials =
            new org.ajoberstar.grgit.Credentials(ext.getGitHubCredConfig().getUsername().getOrNull(), 
                                                 ext.getGitHubCredConfig().getPassword().getOrNull());

             DirectoryProperty cloneDirectory = getProject().getObjects().directoryProperty();
            cloneDirectory.set( getProject().getLayout().getBuildDirectory().dir( DockerXGitHubUtil.getRepositoryNameFromURI( ext.getGitHubConfig().getGitHubURI().get() ) ) );

            DockerXGitHubUtil.addAndCommitFile( gitHubCredentials,
                                                cloneDirectory,
                                                ext.getGitHubConfig().getGitHubBranch().getOrNull(),
                                                ext.getCatalogConfig().getCommitFile().get(),
                                                ext.getCatalogConfig().getCommitMessage().get());
        }
    }
    
    ///
    
    public String toString(DockerXExtension ext) {
        
        if (ext == null )
            return super.toString();
        
        StringBuilder taskSpecs = new StringBuilder(EMPTY_STRING);
        
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_BASE + ") DRYRUN: "          ).append(ext.getDryrun().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_BASE + ") WORKING DIR: "     ).append(ext.getWorkingDirectory().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_CATALOG + ") DISPLAY NAME: " ).append(ext.getCatalogConfig().getDisplayName().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_CATALOG + ") METADATA NAME: ").append(ext.getCatalogConfig().getMetadataName().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_CATALOG + ") IMAGE: "        ).append(ext.getCatalogConfig().getImage().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_CATALOG + ") COMMIT MSG: "   ).append(ext.getCatalogConfig().getCommitMessage().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_CATALOG + ") COMMIT FILE: "  ).append(ext.getCatalogConfig().getCommitFile().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_GITHUB + ") URI: "           ).append(ext.getGitHubConfig().getGitHubURI().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_GITHUB + ") BRANCH: "        ).append(ext.getGitHubConfig().getGitHubBranch().getOrNull()).append(NEW_LINE);
        
        return taskSpecs.toString();
    }

    ///
    
    private final DockerXExtension getPluginExtension() {
        return getProject().getExtensions().findByType(DockerXExtension.class);
    }
}
