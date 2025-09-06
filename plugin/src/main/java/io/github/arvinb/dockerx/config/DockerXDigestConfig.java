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

import org.gradle.api.*;
import org.gradle.api.file.*;
import org.gradle.api.provider.*;
import org.gradle.api.tasks.*;
import io.github.arvinb.dockerx.util.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public interface DockerXDigestConfig {

    /**
     * Handle docker image SHA digests
     */
    @Input @Optional
    public MapProperty<String, java.util.List<String>> getDigests();

    ///

    public class DigestItem {
    
        private final String image;
        private final java.util.List<RegularFileProperty> targetFiles;
        
        private boolean listManifest = false;
        private String imageDigest = EMPTY_STRING;
        private java.util.Map<String, DockerXDockerConfig.PlatformItem> imageDigests = new java.util.HashMap<String, DockerXDockerConfig.PlatformItem>();
        
        /// Constructors
        
        public DigestItem(String image) throws GradleException {
            
            this(image, new java.util.ArrayList<RegularFileProperty>());
        }
        
        public DigestItem(String image, java.util.List<RegularFileProperty>targetFiles) throws GradleException {
            
            this.image = image;
            this.targetFiles = targetFiles;
        }
        
        ///
        
        public void execute(String imageManifest) throws GradleException {
            
            setImageDigest(imageManifest);
            if ( imageDigest.trim().isEmpty() ) return; // Nothing to do
            
            targetFiles.forEach( targetFile -> {
                
                if (targetFile.get().getAsFile().isFile()) {
                
                    try ( java.util.stream.Stream<String> currentContent = java.nio.file.Files.lines(targetFile.get().getAsFile().toPath()) ) {
                        java.util.List<String> newContent = currentContent.map( line -> updateStringImage(line ))
                                                                          .collect(java.util.stream.Collectors.toList());
                        java.nio.file.Files.write(targetFile.get().getAsFile().toPath(), newContent);
                        
                    } catch (Exception ex) {
                        DockerXVerifyUtil.stopGradleException(MESSAGE_DIGEST_EXEC + ex.getMessage());
                    }
                }
                
            });
        }

        ///

        public String updateStringImage( String line ) {

            String updatedLine = line;
            final int slashIndex = line.lastIndexOf('/');

            if ( slashIndex != -1 && ( line.substring(0, slashIndex).contains(".") ) ) {

                String inputImageFullName = line.substring(slashIndex + 1);
                String inputImageName = "";

                if (inputImageFullName.contains("@sha256:")) {
                    inputImageName = inputImageFullName.split("@sha256:")[0];

                } else if (inputImageFullName.contains(":")) {
                    inputImageName = inputImageFullName.split(":")[0];
                }

                if ( inputImageName.equals(getImageName()) )
                    updatedLine = line.replace(inputImageFullName, getImageNameDigest());
            }

            return updatedLine;
        }
        
        ///
        
        public java.util.List<RegularFileProperty>getTargetFiles()                       { return targetFiles;  }
        public String getImage()                                                         { return image;        }
        public boolean isListManifest()                                                  { return listManifest; }
        public java.util.Map<String, DockerXDockerConfig.PlatformItem> getImageDigests() { return imageDigests; }
        
        ///
        
        public String getImageFullName() {
            
            return java.nio.file.Paths.get( getImage() ).getFileName().toString();
        }

        public String getImageName() {
            
            return (getImageFullName().split(":"))[0];
            
        }
        
        public String getImageTag() {
            
            String[] imageChunks = getImageFullName().split(":");
            
            if (imageChunks.length > 1)
                return imageChunks[1];
            return EMPTY_STRING;
        }

        public String getImagePath() {
            return java.nio.file.Paths.get( getImage() ).getParent().toString();
        }
        
        public String getImagePathName() {
            return getImagePath() + "/" + getImageName();
        }

        ///
        
        public String getImageNameDigest() {
            if ( imageDigest.trim().isEmpty() ) return getImageFullName();
            return getImageName() + "@" + imageDigest;
        }
        
        public String getImageManifest() {
            
            if ( this.imageDigest.trim().isEmpty() ) return getImage();
            
            return getImagePath() + "/" + getImageNameDigest();
        }
        
        ///
        
        public void setImageDigest(String imageManifest) throws GradleException {
            
            try {
                
                if (DockerXDigestUtil.isJsonValid(imageManifest)) {
                    
                    // Single image manifest
                    imageDigest = (new com.fasterxml.jackson.databind.ObjectMapper()).readTree(imageManifest).get(DOCKERX_DESCRIPTOR).get((DOCKERX_DIGEST).toLowerCase()).asText();
                    
                } else {
                    
                    // Attempt to parse for a List manifest
                    java.util.stream.Stream<String> imageManifestStream = java.util.Arrays.asList( imageManifest.split("\\r?\\n") ).stream();
                    
                    imageDigest = (imageManifestStream.filter( line -> line.startsWith((DOCKERX_DIGEST + ":")) ).findFirst().orElse(EMPTY_STRING)).replaceFirst("^(" + DOCKERX_DIGEST + ":" + ")\\s*", EMPTY_STRING);
                    listManifest = (!imageDigest.trim().isEmpty());
                    
                    if (isListManifest()) {
                        
                        java.util.
                        List<String> imageDigestKeys = java.util.Arrays.asList( imageManifest.split("\\r?\\n") ).stream()
                                                                        .filter(line -> line.contains(getImage() + "@"))
                                                                        .map(line -> { return new String( (line.split("@"))[1] ); })
                                                                        .collect(java.util.stream.Collectors.toList());
                        java.util.
                        List<String> imageDigestValues = java.util.Arrays.asList( imageManifest.split("\\r?\\n") ).stream()
                                                                        .filter(line -> line.contains((DOCKERX_PLATFORM + ":")))
                                                                        .map(line -> { return line.replaceFirst("^\\s*(" + DOCKERX_PLATFORM + ":" + ")\\s*", ""); })
                                                                        .collect(java.util.stream.Collectors.toList());
                        java.util.
                        List<DockerXDockerConfig.PlatformItem> imageDigestPlatforms = imageDigestValues.stream().map(value -> { 
                        
                            return DockerXDockerConfig.PlatformItem.fromString(value); 
                        
                        }).collect(java.util.stream.Collectors.toList());
                        
                        if (imageDigestKeys.size() == imageDigestPlatforms.size()) {
                            imageDigests = java.util.stream.IntStream.range(0, imageDigestKeys.size()).boxed().collect(java.util.stream.Collectors.toMap( idx -> imageDigestKeys.get(idx), idx -> imageDigestPlatforms.get(idx)));
                        }
                        
                    } else
                        DockerXVerifyUtil.stopGradleException(MESSAGE_DIGEST + imageManifest); // Danger Mr. Robinson we should have a list manifest
                
                }
                
            } catch (Exception ex) {
                DockerXVerifyUtil.stopGradleException(MESSAGE_DIGEST_EXEC + ex.getMessage());
            }
        }
        
        ///
        
        @Override
        public String toString() {
            return getImage() + " | " + getTargetFiles();
        }
    }
    
}