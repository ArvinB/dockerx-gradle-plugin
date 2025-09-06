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

package io.github.arvinb.dockerx.util;

import java.text.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import org.gradle.api.*;
import org.gradle.api.file.*;
import io.github.arvinb.dockerx.config.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXDockerUtil {
    
    public static void pruneAndClearCache(Project project, String builder) {
        
        try { pruneDockerXImages(project);      } catch (Exception ex) { } // Do nothing...
        try { pruneDockerSystemImages(project); } catch (Exception ex) { } // Do nothing...
        try { pruneDockerXVolumes(project);     } catch (Exception ex) { } // Do nothing...
        try { removeBuilder(project, builder);  } catch (Exception ex) { } // Do nothing...
    }
    
    public static boolean removeBuilder(Project project, String builderName) {
        
        try {
            
            Map<String, Boolean> builderList = getDockerXBuilders(project);
            
            if ( builderList.containsKey(builderName) ) {
                removeDockerXBuilder(project, builderName);
                return true;
            }
            
        } catch (Exception ex) {} // Do nothing...
        
        return false;
    }
    
    public static void removeListManifest(Project project, String listManifest) {
        
        try {
            
            removeDockerXListManifest(project, listManifest);
            
        } catch (Exception ex) { } // Do nothing...
    }
    
    public static boolean createAndUseBuilder(Project project, String builderName, boolean multiArchReset) {
        
        try {
            
            Map<String, Boolean> builderList = getDockerXBuilders(project);
            
            if ( builderList.containsKey(builderName) ) {
                
                if (!builderList.get(builderName)) // Builder is not in-use
                    useDockerXBuilder(project, builderName);
                
            } else { // Simply create and use the builder
                
                if ( (DockerXVerifyUtil.isCurrentOS(DockerXVerifyUtil.OS.LINUX)) && (multiArchReset) )
                    activateMultiArch(project);
                
                createDockerXBuilder(project, builderName);
                useDockerXBuilder(project, builderName);
            }
            
            return true;
            
        } catch (Exception ex) {
            removeBuilder(project, builderName);
            DockerXVerifyUtil.stopGradleException(ex.getMessage());
        }
        
        return false;
    }
    
    public static void buildDockerImage(Project project, 
                                        List<String> images, 
                                        RegularFileProperty dockerfile, 
                                        DirectoryProperty workingDir, 
                                        String builder, 
                                        String provenance, 
                                        List<DockerXDockerConfig.PlatformItem> platforms, 
                                        Map<String, String> buildArgs, 
                                        Map<String, RegularFileProperty> secrets, 
                                        String target, 
                                        boolean pushFlag, 
                                        boolean devbuild, 
                                        boolean multiArchReset, 
                                        List<DockerXCredentialConfig.RegistryCredential> registryCredentials) {
        
        if (images.isEmpty() || platforms.isEmpty()) return; // Nothing to do...
        
        try {
            
            if (!builder.isBlank()) { createAndUseBuilder(project, builder, multiArchReset); }
            
            // Get a versioned image from the list
            String versionedImage = (images.stream().filter( aImage -> !aImage.equals(CMD_LINE_LATEST) ).limit(1).collect(Collectors.toList())).get(0);
            
            List<String> builtDockerImages = new ArrayList<String>();
            List<String> buildArguments = new ArrayList<String>();
            if (dockerfile != null) { buildArguments.add(CMD_LINE_OPT_FILE); buildArguments.add(dockerfile.get().getAsFile().getPath()); }
            if ((target != null) && (!target.trim().isEmpty())) { buildArguments.add(CMD_LINE_OPT_TARGET); buildArguments.add(target); }
            if (!buildArgs.isEmpty()) { buildArgs.forEach( (key, value) -> { buildArguments.add(CMD_LINE_OPT_BUILD_ARG); buildArguments.add(key + "=" + value); }); }
            if (!secrets.isEmpty()) { secrets.forEach( (secretId, secret) -> { buildArguments.add(CMD_LINE_OPT_SECRET); buildArguments.add("id=" + secretId + ",src=" + secret.get().getAsFile().getPath()); }); }
            if ((provenance != null) && (!provenance.trim().isEmpty())) { buildArguments.add(CMD_LINE_OPT_PROVENANCE + "=" + provenance); }
            if (pushFlag) { buildArguments.add(CMD_LINE_OPT_PUSH); } else { buildArguments.add(CMD_LINE_OPT_LOAD); }
            
            // Log into registries
            registryCredentials.forEach( cred -> {
                project.getLogger().lifecycle(MessageFormat.format(MESSAGE_DOCKER_LOGIN_REG, cred.getRegistry(), cred.getUsername()));
                loginDockerXAuth(project, cred.getUsername().get(), cred.getPassword().get(), cred.getRegistry().get());
            });

            // Build Docker Image using Local Daemon
            platforms.forEach( platform -> {
                
                List<String> platformArguments = new ArrayList<String>(buildArguments);
                
                platformArguments.add(CMD_LINE_OPT_PLATFORM);
                platformArguments.add(platform.toString());
                
                if (platforms.size() > 1) {
                    String platformImage = versionedImage + "-" + platform.shortString();
                    platformArguments.add(CMD_LINE_OPT_TAG);
                    platformArguments.add(platformImage);
                    builtDockerImages.add(platformImage);
                } else {
                    images.forEach( image -> {
                        platformArguments.add(CMD_LINE_OPT_TAG);
                        platformArguments.add(image);
                        builtDockerImages.add(image);
                    });
                }
                
                buildDockerXImage(project, workingDir, platformArguments);
            });
            
            // Create a List Manifest
            if (pushFlag) {
                if (platforms.size() > 1) {
                    images.forEach( image -> {
                        createDockerXListManifest(project, image, builtDockerImages);
                        pushDockerXListManifest(project, image);
                    });
                }
            }
            
        } catch (Exception ex) {
            DockerXVerifyUtil.stopGradleException(MESSAGE_DOCKER_BUILD + ex.getMessage());
            
        } finally {
            if (!devbuild) removeBuilder(project, builder);
        }
    }
    
    ///
    
    protected static void activateMultiArch(Project project) {
        
        project.getLogger().lifecycle(MESSAGE_CMD_ACTIVATE_MULTIARCH);
        (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_RUN, CMD_LINE_OPT_RM, CMD_LINE_OPT_PRIVILEGED, CMD_LINE_MULTIARCH, CMD_LINE_OPT_RESET, CMD_LINE_OPT_PERSISTENT, CMD_LINE_YES)).execute();
    }
    
    protected static void pruneDockerXImages(Project project) {
        
        project.getLogger().lifecycle(MESSAGE_CMD_PRUNE_IMAGES);
        (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_BUILDX, CMD_LINE_PRUNE, CMD_LINE_OPT_ALL, CMD_LINE_OPT_FORCE)).execute();
    }
    
    protected static void pruneDockerSystemImages(Project project) {
        
        project.getLogger().lifecycle(MESSAGE_CMD_PRUNE_SYS_IMAGES);
        (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_SYSTEM, CMD_LINE_PRUNE, CMD_LINE_OPT_ALL, CMD_LINE_OPT_FORCE)).execute();
    }
    
    protected static void pruneDockerXVolumes(Project project) {
        
        project.getLogger().lifecycle(MESSAGE_CMD_PRUNE_VOLUMES);
        (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_VOLUME, CMD_LINE_PRUNE, CMD_LINE_OPT_FORCE)).execute();
    }
    
    protected static void createDockerXBuilder(Project project, String builderName) {
        
        project.getLogger().lifecycle(MESSAGE_CMD_CREATE_BUILDER);
        (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_BUILDX, CMD_LINE_CREATE, CMD_LINE_OPT_NAME, builderName)).execute();
    }
    
    protected static void useDockerXBuilder(Project project, String builderName) {
        
        project.getLogger().lifecycle(MESSAGE_CMD_USE_BUILDER);
        (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_BUILDX, CMD_LINE_USE, builderName)).execute();
    }
    
    protected static void removeDockerXBuilder(Project project, String builderName) {
        
        project.getLogger().lifecycle(MESSAGE_CMD_REMOVE_BUILDER);
        (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_BUILDX, CMD_LINE_RM, builderName)).execute();
    }
    
    protected static void loginDockerXAuth(Project project, String username, String password, String registry) {
        
        project.getLogger().lifecycle(MESSAGE_CMD_LOGIN_DOCKER_XAUTH);
        (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_LOGIN, CMD_LINE_OPT_U, username, CMD_LINE_OPT_P, password, registry)).execute();
    }
    
    protected static void pushDockerXListManifest(Project project, String versionedImage) {

        project.getLogger().lifecycle(MESSAGE_CMD_PUSH_MANIFEST);
        (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_MANIFEST, CMD_LINE_PUSH, CMD_LINE_OPT_PURGE, versionedImage)).execute();
    }
    
    protected static void removeDockerXListManifest(Project project, String versionedImage) {

        project.getLogger().lifecycle(MESSAGE_CMD_REMOVE_MANIFEST);
        (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_MANIFEST, CMD_LINE_RM, versionedImage)).execute();
    }
    
    protected static String inspectDockerXImage(Project project, String image) {

        project.getLogger().lifecycle(MESSAGE_CMD_INSPECT_IMAGE);
        return (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_MANIFEST, CMD_LINE_INSPECT, image, CMD_LINE_OPT_VERBOSE)).execute().cmdOutput;
    }
    
    protected static String inspectDockerXListManifest(Project project, String image) {
        
        project.getLogger().lifecycle(MESSAGE_CMD_INSPECT_MANIFEST);
        return (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_BUILDX, CMD_LINE_IMAGETOOLS, CMD_LINE_INSPECT, image)).execute().cmdOutput;
    }
    
    protected static void buildDockerXImage(Project project, DirectoryProperty workingDir, List<String> dockerXArgs) {
        
        project.getLogger().lifecycle(MESSAGE_CMD_BUILD_IMAGE);

        List<String> dockerCommand = new ArrayList<String>(dockerXArgs);
        dockerCommand.add(0, CMD_LINE_DOCKER);
        dockerCommand.add(1, CMD_LINE_BUILDX);
        dockerCommand.add(2, CMD_LINE_BUILD);
        dockerCommand.add(".");
        
        (new DockerCmdLineUtil( project, dockerCommand.toArray(new String[0]) )).execute(workingDir.get().getAsFile(), false);
    }

    protected static String pushDockerXImage(Project project, String image) {

        project.getLogger().lifecycle(MESSAGE_CMD_PUSH_IMAGE);
        return (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_PUSH, CMD_LINE_OPT_ALL_TAGS, image)).execute().cmdOutput;
    }
    
    protected static void createDockerXListManifest(Project project, String versionedImage, List<String> taggedImages) {
        
        project.getLogger().lifecycle(MESSAGE_CMD_CREATE_MANIFEST);

        List<String> dockerCommand = new ArrayList<String>(taggedImages);
        dockerCommand.add(0, CMD_LINE_DOCKER);
        dockerCommand.add(1, CMD_LINE_MANIFEST);
        dockerCommand.add(2, CMD_LINE_CREATE);
        dockerCommand.add(3, CMD_LINE_OPT_AMEND);
        dockerCommand.add(4, versionedImage);
        
        (new DockerCmdLineUtil( project, dockerCommand.toArray(new String[0]) )).execute();
    }
    
    protected static Map<String, Boolean> getDockerXBuilders(Project project) {
        
        project.getLogger().lifecycle(MESSAGE_CMD_GET_BUILDERS);

        DockerXCmdLineUtil.ProcessResult processResult = (new DockerCmdLineUtil(project, CMD_LINE_DOCKER, CMD_LINE_BUILDX, CMD_LINE_LS)).execute();
        Map<String, Boolean> builderList = DockerCmdLineUtil.processBuilderList(processResult);
        
        if (builderList == null) {
            DockerXVerifyUtil.stopGradleException(processResult.cmdOutput);
        }
        
        return builderList;
    }
    
    ///
    
    private static class DockerCmdLineUtil extends DockerXCmdLineUtil {
        
        /// Constructor
        
        public DockerCmdLineUtil(Project project, String... args) { super( project, args ); }
        
        ///
        
        @Override
        public ProcessResult execute(@Nullable File workingDir, boolean returnOutput) {
            
            ProcessResult processResult = null;
            
            try {
                
                processResult = super.execute(workingDir, returnOutput);
                if (processResult.exitCode != 0) {
                    DockerXVerifyUtil.stopGradleException(processResult.cmdOutput);
                }
                
            } catch (Exception ex) {
                DockerXVerifyUtil.stopGradleException(MESSAGE_DOCKER_BUILD_EXEC + ex.getMessage());
            }
            
            return processResult;
        }
        
        public ProcessResult execute() { return execute(null, true); }
        
        ///
        
        public static Map<String, Boolean> processBuilderList(ProcessResult processResult) {
            
            if (processResult.exitCode == 0) {
                
                // Name, In-use
                Map<String, Boolean> builderList = new HashMap<String, Boolean>();
                
                processResult.getOutputLines().stream().skip(1).forEach((line) -> {
                    
                    String[] parsedLine = line.split("\\s+");
                    
                    if ( parsedLine.length > 1 ) {
                        
                        String firstItem = parsedLine[0];
                        String secondItem = parsedLine[1];
                        
                        if (firstItem.trim().isEmpty()) return; // Skip Iteration
                        
                        if (secondItem.compareTo(CMD_LINE_DOCKER_CONTAINER) == 0) {
                            if (firstItem.endsWith("*")) {
                                builderList.put(firstItem.replaceAll(".$", ""), true);
                            } else {
                                builderList.put(firstItem, false);
                            }
                        }
                    }
                    
                });
                return builderList;
            }
            return null;
        }
    }
}
