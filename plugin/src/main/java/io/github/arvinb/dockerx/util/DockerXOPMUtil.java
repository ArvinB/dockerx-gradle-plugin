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

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;
import javax.annotation.*;
import org.gradle.api.*;
import org.gradle.api.file.*;
import io.github.arvinb.dockerx.config.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXOPMUtil extends DockerXDockerUtil {
    
    public static String getOPMVersion(Project project) {
        
        String opmVersion = EMPTY_STRING;
        
        DockerXCmdLineUtil.ProcessResult processResult = (new OPMCmdLineUtil(project, CMD_LINE_OPM, CMD_LINE_VERSION)).execute();
        Map<String, String> versionList = OPMCmdLineUtil.processVersionList(processResult);
        
        if ((versionList != null) && (!versionList.isEmpty()))
            opmVersion = versionList.get(CMD_LINE_OPMVERSION).replaceAll("[^\\d.]", EMPTY_STRING);
        
        return opmVersion;
    }
    
    public static void buildBundleImage(Project project,
                                        List<String> bundleImages,
                                        DirectoryProperty bundleDir,
                                        DirectoryProperty manifestsDir,
                                        String packageName,
                                        List<String> channels,
                                        String defaultChannel,
                                        String imageBuilder,
                                        boolean overwriteMetadataFlag,
                                        boolean pushFlag,
                                        List<DockerXCredentialConfig.RegistryCredential> registryCredentials) {
        
        if (bundleImages.isEmpty() || packageName.isEmpty() || defaultChannel.isEmpty() || channels.isEmpty() || !bundleDir.isPresent() || !manifestsDir.isPresent()) return; // Nothing to do...
        
        try {
            
            // Build Bundle Annotations and Image
            List<String> opmArguments = new ArrayList<String>();
            
            opmArguments.add(CMD_LINE_OPT_DIRECTORY);
            opmArguments.add(manifestsDir.getAsFile().get().getPath());
            opmArguments.add(CMD_LINE_OPT_TAG);
            opmArguments.add(bundleImages.get(0));
            opmArguments.add(CMD_LINE_OPT_PACKAGE);
            opmArguments.add(packageName);
            opmArguments.add(CMD_LINE_OPT_CHANNELS);
            opmArguments.add( channels.stream().collect(Collectors.joining(COMMA))  );
            opmArguments.add(CMD_LINE_OPT_DEFAULT);
            opmArguments.add(defaultChannel);
            opmArguments.add(CMD_LINE_OPT_IMAGE_BUILDER);
            opmArguments.add(imageBuilder);
            
            buildOPMBundleImage(project, opmArguments, bundleDir.getAsFile().get());
            
            // Push Bundle Image
            if (pushFlag) {
                
                // Log into registries
                registryCredentials.forEach( cred -> {
                    project.getLogger().lifecycle(MessageFormat.format(MESSAGE_DOCKER_LOGIN_REG, cred.getRegistry(), cred.getUsername()));
                    loginDockerXAuth(project, cred.getUsername().get(), cred.getPassword().get(), cred.getRegistry().get());
                });
                
                pushDockerXImage(project, bundleImages.get(0));
            }
            
        } catch (Exception ex) {
            DockerXVerifyUtil.stopGradleException(MESSAGE_OPM_BUNDLE_BUILD + ex.getMessage());
        }
    }

    public static void initOLMPackage(Project project,
                                        DirectoryProperty workingDir,
                                        String outputPrefix,
                                        String format,
                                        String packageName,
                                        String defaultChannel,
                                        RegularFile catalogReadme,
                                        RegularFile catalogIcon) {
        
        if (packageName.isEmpty() ) return; // Nothing to do...
        
        try {

            // Init OLM Package
            List<String> opmArguments = new ArrayList<String>();

            opmArguments.add(packageName);
            if (defaultChannel != null) { opmArguments.add(CMD_LINE_OPT_CHANNEL_DEF); opmArguments.add(defaultChannel);                      }
            if (catalogReadme != null)  { opmArguments.add(CMD_LINE_OPT_DESCRIPTION); opmArguments.add(catalogReadme.getAsFile().getPath()); }
            if (catalogIcon != null)    { opmArguments.add(CMD_LINE_OPT_ICON);        opmArguments.add(catalogIcon.getAsFile().getPath());   }
            opmArguments.add(CMD_LINE_OPT_OUTPUT);
            opmArguments.add(format);

            String outputFile = outputPrefix  + "." + format;
            saveOPMRender(project, workingDir, CMD_LINE_INIT, opmArguments, outputFile);
            
        } catch (Exception ex) {
            DockerXVerifyUtil.stopGradleException(MESSAGE_OPM_INIT + ex.getMessage());
        }
    }
    
    public static void addRegistryBundlesDb(Project project, 
                                            List<String> bundleImages, 
                                            DirectoryProperty bundleDir, 
                                            String containerTool, 
                                            String mode, 
                                            List<DockerXCredentialConfig.RegistryCredential> registryCredentials) {
        
        if (bundleImages.isEmpty() || !bundleDir.isPresent()) return; // Nothing to do...
        
        try {
            
            // Log into registries
            registryCredentials.forEach( cred -> {
                project.getLogger().lifecycle(MessageFormat.format(MESSAGE_DOCKER_LOGIN_REG, cred.getRegistry(), cred.getUsername()));
                loginDockerXAuth(project, cred.getUsername().get(), cred.getPassword().get(), cred.getRegistry().get());
            });
            
            List<String> digestImages = DockerXDigestUtil.convertImageTagsToDigests(project, bundleImages, registryCredentials);
            
            // Add Bundles into a Registry db
            List<String> opmArguments = new ArrayList<String>();
            
            opmArguments.add(CMD_LINE_OPT_BUNDLE_IMAGES);
            opmArguments.add(digestImages.stream().collect(Collectors.joining(COMMA)));
            opmArguments.add(CMD_LINE_OPT_CONTAINER_TOOL);
            opmArguments.add(containerTool);
            opmArguments.add(CMD_LINE_OPT_MODE);
            opmArguments.add(mode);
            
            addOPMRegistryBundlesDb(project, opmArguments, bundleDir.getAsFile().get());
            
        } catch (Exception ex) {
            DockerXVerifyUtil.stopGradleException(MESSAGE_OPM_REGISTRY_ADD + ex.getMessage());
        }
    }

    public static void renderBundleImage(Project project,
                                        DirectoryProperty workingDir,
                                        List<String> bundleSources,
                                        String outputPrefix,
                                        String outputFormat,
                                        List<DockerXCredentialConfig.RegistryCredential> registryCredentials) {
        
        if (bundleSources.isEmpty() ) return; // Nothing to do...
        
        try {

            // Log into registries
            registryCredentials.forEach( cred -> {
                project.getLogger().lifecycle(MessageFormat.format(MESSAGE_DOCKER_LOGIN_REG, cred.getRegistry(), cred.getUsername()));
                loginDockerXAuth(project, cred.getUsername().get(), cred.getPassword().get(), cred.getRegistry().get());
            });
            
            List<String> opmArguments = new ArrayList<String>();

            // Render Bundle Images
            bundleSources.stream().forEach( bundleSource -> {
                opmArguments.add(bundleSource);
                opmArguments.add(CMD_LINE_OPT_OUTPUT);
                opmArguments.add(outputFormat);
                String imageTag = bundleSource.substring(bundleSource.lastIndexOf(":") + 1);
                String outputFile = outputPrefix + "-" + imageTag + "." + outputFormat;
                saveOPMRender(project, workingDir, CMD_LINE_RENDER, opmArguments, outputFile);
                opmArguments.clear();
            });
            
        } catch (Exception ex) {
            DockerXVerifyUtil.stopGradleException(MESSAGE_OPM_RENDER_BUNDLE + ex.getMessage());
        }
    }
    
    public static void validateBundleImage(Project project, 
                                           List<String> bundleImages, 
                                           List<DockerXCredentialConfig.RegistryCredential> registryCredentials) {
        
        if (bundleImages.isEmpty()) return; // Nothing to do...
        
        try {
            
            // Log into registries
            registryCredentials.forEach( cred -> {
                project.getLogger().lifecycle(MessageFormat.format(MESSAGE_DOCKER_LOGIN_REG, cred.getRegistry(), cred.getUsername()));
                loginDockerXAuth(project, cred.getUsername().get(), cred.getPassword().get(), cred.getRegistry().get());
            });
            
            bundleImages.stream().forEach( bundleImage -> {
                validateOPMBundleImage(project, bundleImage);
            });
            
        } catch (Exception ex) {
            DockerXVerifyUtil.stopGradleException(MESSAGE_OPM_VALIDATE + ex.getMessage());
        }
    }
    
    ///
    
    protected static void buildOPMBundleImage(Project project, List<String> opmArgs, File workingDir) {
        
        List<String> opmCommand = new ArrayList<String>(opmArgs);
        opmCommand.add(0, CMD_LINE_OPM);
        opmCommand.add(1, CMD_LINE_ALPHA);
        opmCommand.add(2, CMD_LINE_BUNDLE);
        opmCommand.add(3, CMD_LINE_BUILD);
        
        (new OPMCmdLineUtil( project, opmCommand.toArray(new String[0]) )).execute(workingDir, true);
    }
    
    protected static void addOPMRegistryBundlesDb(Project project, List<String> opmArgs, File workingDir) {
        
        List<String> opmCommand = new ArrayList<String>(opmArgs);
        opmCommand.add(0, CMD_LINE_OPM);
        opmCommand.add(1, CMD_LINE_REGISTRY);
        opmCommand.add(2, CMD_LINE_ADD);
        (new OPMCmdLineUtil( project, opmCommand.toArray(new String[0]) )).execute(workingDir, true);
    }

    protected static void saveOPMRender(Project project, DirectoryProperty workingDir, String opmAction, List<String> opmArgs, String outputFile) {

        List<String> opmCommand = new ArrayList<String>(opmArgs);
        opmCommand.add(0, CMD_LINE_OPM);
        opmCommand.add(1, opmAction);

        File file = new File(workingDir.get().getAsFile() + "/" + outputFile); 
        if (file.exists()) file.delete(); 

        DockerXCmdLineUtil.ProcessResult processResult = (new OPMCmdLineUtil( project, opmCommand.toArray(new String[0]) )).execute(workingDir.get().getAsFile(), true);
        
        if ( outputFile != null) {
            List<String> outputOPM = processResult.getOutputLines();
            try (BufferedWriter outputFileWr = new BufferedWriter(new FileWriter(workingDir.get().getAsFile() + "/" + outputFile, true))) {
                outputOPM.forEach( outputLine -> {
                    try {
                        outputFileWr.write(outputLine);
                        outputFileWr.newLine();
                    } catch (Exception ignore) {}
                });
            } catch (Exception ex) {
                DockerXVerifyUtil.stopGradleException(MESSAGE_OPM_STORE_OUTPUT + ex.getMessage());
            }
        }
    }
    
    protected static void validateOPMBundleImage(Project project, String image) {
        (new OPMCmdLineUtil(project, CMD_LINE_OPM, CMD_LINE_ALPHA, CMD_LINE_BUNDLE, CMD_LINE_VALIDATE, CMD_LINE_OPT_TAG, image, CMD_LINE_OPT_IMAGE_BUILDER, CMD_LINE_DOCKER)).execute();
    }

    public static class OPMCmdLineUtil extends DockerXCmdLineUtil {
        
        /// Constructor
        
        public OPMCmdLineUtil(Project project, String... args) { super( project, args ); }
        
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
                DockerXVerifyUtil.stopGradleException(MESSAGE_OPM_EXEC + ex.getMessage());
            }
            
            return processResult;
        }
        
        public ProcessResult execute() { return execute(null, true); }
        
        ///
        
        public static Map<String, String> processVersionList(ProcessResult processResult) {
            
            if (processResult.exitCode == 0) {
                
                // Key, Value
                Map<String, String> versionMap = new HashMap<String, String>();
                
                Optional<String> firstLine = processResult.getOutputLines().stream().findFirst();
                
                if ( firstLine.isPresent() ) {
                    
                    try {
                        
                        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
                        Matcher matcher = pattern.matcher(firstLine.get());
                        
                        if (matcher.find()) {
                            
                            List<String> versionList = Stream.of( matcher.group(1).split(",") ).map(String::trim).collect(Collectors.toList() );
                            
                            versionList.stream().forEach((items) -> {
                                String[] item = items.split(":");
                                versionMap.put(item[0].trim().replace("\"", EMPTY_STRING), item[1].trim().replace("\"", EMPTY_STRING));
                            });
                        }
                        
                    } catch (Exception ex) {
                        DockerXVerifyUtil.stopGradleException(MESSAGE_OPM_VERSION_LIST + ex.getMessage());
                    }
                }
                
                return versionMap;
            }
            return null;
        }
    }
}
