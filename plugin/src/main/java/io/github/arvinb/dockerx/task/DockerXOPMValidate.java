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

public class DockerXOPMValidate extends DefaultTask {
    
    @TaskAction
    public void opmTaskAction() {
    
        final DockerXExtension ext = getPluginExtension();
        getProject().getLogger().lifecycle(this.toString(ext));
        
        if ( !ext.getDryrun().get() ) {
            
            if ( !ext.getSkip().get() ) {
            
                // Verify the environment
                DockerXVerifyUtil.verifyOperatorPackageManager(getProject());
                DockerXVerifyUtil.verifyDockerEnvironment();
                
                // Clean the environment
                if ( ext.getOpmSpecs().getClean().get() )
                    DockerXDockerUtil.pruneAndClearCache(getProject(), ext.getDockerSpecs().getBuilder().get());
                
                // Build Bundle Image
                DockerXOPMUtil.validateBundleImage(getProject(),                                                // Gradle Project
                                                   ext.getOpmSpecs().getBundleImages().get(),                   // Bundle image
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
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") ACTION: ").append(ext.getOpmSpecs().getAction().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") CLEAN: ").append(ext.getOpmSpecs().getClean().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") BUNDLE DIR: ").append(ext.getOpmSpecs().getBundleDir().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") PACKAGE NAME: ").append(ext.getOpmSpecs().getPackageName().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") DEFAULT CHANNEL: ").append(ext.getOpmSpecs().getDefaultChannel().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") TOOL: ").append(ext.getOpmSpecs().getTool().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") MODE: ").append(ext.getOpmSpecs().getMode().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") OVERWRITE METADATA: ").append(ext.getOpmSpecs().getOverwriteMetadata().getOrNull()).append(NEW_LINE);
        
        counter.set(0);
        ext.getOpmSpecs().getChannels().get().forEach( channel -> {
            taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") CHANNEL[" + counter + "]: ").append(channel).append(NEW_LINE);
            counter.getAndIncrement();
        });
        
        counter.set(0);
        ext.getOpmSpecs().getBundleImages().get().forEach( bundleImage -> {
            taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_OPM + ") BUNDLE IMAGE[" + counter + "]: ").append(bundleImage).append(NEW_LINE);
            counter.getAndIncrement();
        });
        
        return taskSpecs.toString();
    }

    ///
    
    private final DockerXExtension getPluginExtension() {
        return getProject().getExtensions().findByType(DockerXExtension.class);
    }
}
