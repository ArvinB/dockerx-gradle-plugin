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
import java.util.*;
import org.gradle.api.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import io.github.arvinb.dockerx.config.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXDigestUtil extends DockerXDockerUtil {
    
    public static boolean isJsonValid(final String jsonString) {
        
        try {
            
            final JsonParser jsonParser = (new ObjectMapper()).getFactory().createParser(jsonString);
            while ( jsonParser.nextToken() != null ) {}
            return true;
            
        } catch (Exception ex) { }
        
        return false;
    }
    
    public static boolean isJsonObject(final String jsonString) {
        
        try {
            
            final JsonNode jsonNode = (new ObjectMapper()).readTree(jsonString);
            return jsonNode.isObject();
            
        } catch (Exception ex) { }
        
        return false;
    }
    
    public static boolean isJsonArray(final String jsonString) {
        
        try {
            
            final JsonNode jsonNode = (new ObjectMapper()).readTree(jsonString);
            return jsonNode.isArray();
            
        } catch (Exception ex) { }
        
        return false;
    }

    public static List<String> convertImageTagsToDigests(Project project, List<String> images, List<DockerXCredentialConfig.RegistryCredential> registryCredentials) {
        
        if (images.isEmpty()) return images; // Nothing to do...
        
        // Log into registries
        registryCredentials.forEach( cred -> {
            project.getLogger().lifecycle(MessageFormat.format(MESSAGE_DOCKER_LOGIN_REG, cred.getRegistry(), cred.getUsername()));
            loginDockerXAuth(project, cred.getUsername().get(), cred.getPassword().get(), cred.getRegistry().get());
        });

        List<String> digestImages = new ArrayList<String>();
        
        images.stream().forEach( image -> {
            
            DockerXDigestConfig.DigestItem digestItem = new DockerXDigestConfig.DigestItem(image);
            String imageInspect = inspectDockerXImage(project, digestItem.getImage());
            
            if ( DockerXDigestUtil.isJsonValid(imageInspect) ) {
                
                if (DockerXDigestUtil.isJsonObject(imageInspect))
                    digestItem.setImageDigest(inspectDockerXImage(project, digestItem.getImage()));
                
                else if (DockerXDigestUtil.isJsonArray(imageInspect))
                    digestItem.setImageDigest(inspectDockerXListManifest(project, digestItem.getImage()));
            }
            
            digestImages.add( digestItem.getImageManifest() );
            
        });
        
        return digestImages;
    }
    
    public static void injectImageDigests(Project project, List<DockerXDigestConfig.DigestItem> digestItems, List<DockerXCredentialConfig.RegistryCredential> registryCredentials) {
        
        if (digestItems.isEmpty()) return; // Nothing to do...
        
        // Log into registries
        registryCredentials.forEach( cred -> {
            project.getLogger().lifecycle(MessageFormat.format(MESSAGE_DOCKER_LOGIN_REG, cred.getRegistry(), cred.getUsername()));
            loginDockerXAuth(project, cred.getUsername().get(), cred.getPassword().get(), cred.getRegistry().get());
        });
            
        digestItems.stream().forEach( digestItem -> {
            
            String imageInspect = inspectDockerXImage(project, digestItem.getImage());
            
            if ( DockerXDigestUtil.isJsonValid(imageInspect) ) {
                
                if (DockerXDigestUtil.isJsonObject(imageInspect))
                    digestItem.execute(inspectDockerXImage(project, digestItem.getImage()));
                
                else if (DockerXDigestUtil.isJsonArray(imageInspect))
                    digestItem.execute(inspectDockerXListManifest(project, digestItem.getImage()));
                
                digestItem.getTargetFiles().forEach( targetFile -> {
                    
                    project.getLogger().lifecycle(MessageFormat.format(MESSAGE_DIGEST_SUBST, digestItem.getImageNameDigest(), targetFile.get().getAsFile().getName()));
                    
                });
            }
        });
    }
}
