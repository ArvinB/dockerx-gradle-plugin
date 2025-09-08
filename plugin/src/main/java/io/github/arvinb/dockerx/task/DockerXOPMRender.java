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

public class DockerXOPMRender extends DefaultTask {
    
    @TaskAction
    public void opmTaskAction() {
    
        final DockerXExtension ext = getPluginExtension();
        getProject().getLogger().lifecycle(this.toString(ext));

        if ( !ext.getDryrun().get() ) {
            
            // Verify the environment
            DockerXVerifyUtil.verifyOperatorPackageManager(getProject());
            DockerXVerifyUtil.verifyDockerEnvironment();

            // Clean the environment
            if ( ext.getOpmSpecs().getClean().get() )
                DockerXDockerUtil.pruneAndClearCache(getProject(), ext.getDockerSpecs().getBuilder().get());
                    
            // Render bundle image to create operator.yaml
            DockerXOPMUtil.renderBundleImage(getProject(),                                                   // Gradle Project
                                                ext.getWorkingDirectory(),                                   // Working directory 
                                                ext.getOpmSpecs().getBundleSources().get(),                  // List of Bundle Sources
                                                ext.getOpmSpecs().getOutputPrefix().get(),                   // Output file prefix
                                                ext.getOpmSpecs().getOutputFormat().get(),                   // Output format (json|yaml) (default "yaml")
                                                ext.getRegistryCredentials(ext.getDockerCredSpecs()).get()); // Docker Registry Credentials
        }
    }

    ///

    public String toString(DockerXExtension ext) {
        
        if (ext == null )
            return super.toString();
        
        StringBuilder taskSpecs = new StringBuilder(EMPTY_STRING);
        AtomicInteger counter = new AtomicInteger(0);
        
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_BASE + ") DRYRUN: ").append(ext.getDryrun().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_BASE + ") WORKING DIR: ").append(ext.getWorkingDirectory().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") CLEAN: ").append(ext.getOpmSpecs().getClean().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") OUTPUT PREFIX: ").append(ext.getOpmSpecs().getOutputPrefix().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") FORMAT: ").append(ext.getOpmSpecs().getOutputFormat().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") BUNDLE SOURCES: ").append(ext.getOpmSpecs().getBundleSources().getOrNull()).append(NEW_LINE);
        
        counter.set(0);
        ext.getOpmSpecs().getBundleSources().get().forEach( bundleSource -> {
            taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") BUNDLE SOURCE[" + counter + "]: ").append(bundleSource).append(NEW_LINE);
            counter.getAndIncrement();
        });
        
        return taskSpecs.toString();
    }

    ///
    
    private final DockerXExtension getPluginExtension() {
        return getProject().getExtensions().findByType(DockerXExtension.class);
    }
}
