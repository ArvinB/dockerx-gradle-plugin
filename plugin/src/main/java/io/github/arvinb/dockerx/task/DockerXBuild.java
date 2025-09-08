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

import java.util.concurrent.atomic.*;
import org.gradle.api.*;
import org.gradle.api.tasks.*;
import io.github.arvinb.dockerx.*;
import io.github.arvinb.dockerx.util.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXBuild extends DefaultTask {
    
    @TaskAction
    public void buildTaskAction() {
        
        final DockerXExtension ext = getPluginExtension();
        getProject().getLogger().lifecycle(this.toString(ext));
        
        if ( !ext.getDryrun().get() ) {
            
            if ( !ext.getSkip().get() ) {
            
                // Verify the environment
                DockerXVerifyUtil.verifyDockerEnvironment();
                
                // Clean the environment
                if ( ext.getDockerSpecs().getClean().get() )
                    DockerXDockerUtil.pruneAndClearCache(getProject(), ext.getDockerSpecs().getBuilder().get());
                
                // Build Docker Image
                DockerXDockerUtil.buildDockerImage(getProject(),                                                // Gradle Project
                                                   ext.getDockerSpecs().getImages().get(),                      // Docker Images
                                                   ext.getDockerfile(),                                         // Dockerfile
                                                   ext.getWorkingDirectory(),                                   // Working Directory
                                                   ext.getDockerSpecs().getBuilder().get(),                     // Builder Name
                                                   ext.getDockerSpecs().getProvenance().getOrNull(),            // Provenance
                                                   ext.getPlatformItems().get(),                                // Platforms
                                                   ext.getDockerSpecs().getBuildArgs().get(),                   // Build Arguments
                                                   ext.getSecrets().get(),                                      // Docker Secrets
                                                   ext.getDockerSpecs().getTarget().getOrNull(),                // Docker Target
                                                   ext.getDockerSpecs().getPush().get(),                        // Push Flag
                                                   ext.getDockerSpecs().getDevbuild().get(),                    // Dev Build Flag
                                                   ext.getDockerSpecs().getMultiArchReset().get(),              // Multi Arch Reset Flag
                                                   ext.getRegistryCredentials(ext.getDockerCredSpecs()).get()); // Docker Registry Credentials
            }
        }
    }
    
    ///
    
    public String toString(DockerXExtension ext) {
        
        if (ext == null )
            return super.toString();
        
        StringBuilder taskSpecs = new StringBuilder(EMPTY_STRING);
        AtomicInteger counter = new AtomicInteger(0);
        
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_BASE + ") DRYRUN: ").append(ext.getDryrun().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_SKIP + ") SKIP: ").append(ext.getSkip().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_BASE + ") WORKING DIR: ").append(ext.getWorkingDirectory().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") CLEAN: ").append(ext.getDockerSpecs().getClean().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") DEVBUILD: ").append(ext.getDockerSpecs().getDevbuild().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") MULTIARCH REFRESH: ").append(ext.getDockerSpecs().getMultiArchReset().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") PUSH: ").append(ext.getDockerSpecs().getPush().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") BUILDER: ").append(ext.getDockerSpecs().getBuilder().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") PROVENANCE: ").append(ext.getDockerSpecs().getProvenance().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") TARGET: ").append(ext.getDockerSpecs().getTarget().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") DOCKERFILE: ").append(ext.getDockerSpecs().getDockerfile().getOrNull()).append(NEW_LINE);
        
        counter.set(0);
        ext.getDockerSpecs().getBuildArgs().get().forEach( (argKey, argValue) -> {
            taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") ARG KEY[" + counter + "]: ").append(argKey).append(NEW_LINE);
            taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") ARG VALUE[" + counter + "]: ").append(argValue).append(NEW_LINE);
            counter.getAndIncrement();
        });

        counter.set(0);
        ext.getSecrets().get().forEach( (secretKey, secretValue) -> {
            taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") SECRET KEY[" + counter + "]: ").append(secretKey).append(NEW_LINE);
            taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") SECRET VALUE[" + counter + "]: ").append(secretValue.get().getAsFile().getPath()).append(NEW_LINE);
            counter.getAndIncrement();
        });
        
        counter.set(0);
        ext.getDockerSpecs().getPlatforms().get().forEach( platform -> {
            taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") PLATFORM[" + counter + "]: ").append(platform).append(NEW_LINE);
            counter.getAndIncrement();
        });
        
        counter.set(0);
        ext.getDockerSpecs().getImages().get().forEach( image -> {
            taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_DOCKERX_BUILD + ") IMAGE[" + counter + "]: ").append(image).append(NEW_LINE);
            counter.getAndIncrement();
        });
        
        return taskSpecs.toString();
    }

    ///
    
    private final DockerXExtension getPluginExtension() {
        return getProject().getExtensions().findByType(DockerXExtension.class);
    }
}
