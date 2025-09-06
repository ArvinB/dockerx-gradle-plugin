/*******************************************************************************
 Licensed Materials - Property of IBM
 (C) Copyright IBM Corporation 2024. All Rights Reserved.
 *
 Note to U.S. Government Users Restricted Rights:
 Use, duplication or disclosure restricted by GSA ADP Schedule
 Contract with IBM Corp.
*******************************************************************************/

package io.github.arvinb.dockerx.task;

import org.gradle.api.*;
import org.gradle.api.file.*;
import org.gradle.api.tasks.*;
import io.github.arvinb.dockerx.*;
import io.github.arvinb.dockerx.util.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXGitHub extends DefaultTask {
    
    @TaskAction
    public void gitHubTaskAction() {
        
        final DockerXExtension ext = getPluginExtension();
        getProject().getLogger().lifecycle(this.toString(ext));
        
        if ( !ext.getDryrun().get() ) {
            
            if ( !ext.getSkip().get() ) {
            
                DockerXVerifyUtil.verifyGitHubRepo(ext.getGitHubConfig().getGitHubURI().get());
                
                DirectoryProperty cloneDirectory = getProject().getObjects().directoryProperty();
                cloneDirectory.set( getProject().getLayout().getBuildDirectory().dir( DockerXGitHubUtil.getRepositoryNameFromURI( ext.getGitHubConfig().getGitHubURI().get() ) ) );
                
                org.ajoberstar.grgit.Credentials gitHubCredentials =
                new org.ajoberstar.grgit.Credentials(ext.getGitHubCredConfig().getUsername().getOrNull(), 
                                                     ext.getGitHubCredConfig().getPassword().getOrNull());
                
                DockerXGitHubUtil.cloneAndCheckoutRepository(gitHubCredentials, 
                                                             ext.getGitHubConfig().getGitHubURI().get(), 
                                                             cloneDirectory, 
                                                             ext.getGitHubConfig().getGitHubBranch().getOrNull());
            }
        }
    }
    
    ///
    
    public String toString(DockerXExtension ext) {
        
        if (ext == null )
            return super.toString();
        
        StringBuilder taskSpecs = new StringBuilder(EMPTY_STRING);
        
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_BASE + ") DRYRUN: "       ).append(ext.getDryrun().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_SKIP + ") SKIP: "         ).append(ext.getSkip().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_BASE + ") WORKING DIR: "  ).append(ext.getWorkingDirectory().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_GITHUB + ") URI: "        ).append(ext.getGitHubConfig().getGitHubURI().getOrNull()).append(NEW_LINE);
        taskSpecs.append(LABEL_DOCKERX).append("(" + LABEL_GITHUB + ") BRANCH: "     ).append(ext.getGitHubConfig().getGitHubBranch().getOrNull()).append(NEW_LINE);
        
        return taskSpecs.toString();
    }

    ///
    
    private final DockerXExtension getPluginExtension() {
        return getProject().getExtensions().findByType(DockerXExtension.class);
    }
}
