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
import java.util.regex.*;
import org.gradle.api.*;
import io.github.arvinb.dockerx.config.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXCatalogUtil extends DockerXDockerUtil {
    
    public static String getCatalogSourceData(Project project, 
                                              Boolean dryRun, 
                                              String metadataName, 
                                              String displayName, 
                                              String image, 
                                              List<DockerXCredentialConfig.RegistryCredential> registryCredentials) {
        
        DockerXDigestConfig.DigestItem catalogDigestItem = new DockerXDigestConfig.DigestItem(image);
        
        if ( !dryRun ) {
            
            DockerXVerifyUtil.verifyDockerEnvironment();
            
            // Log into registries
            registryCredentials.forEach( cred -> {
                project.getLogger().lifecycle(MessageFormat.format(MESSAGE_DOCKER_LOGIN_REG, cred.getRegistry(), cred.getUsername()));
                loginDockerXAuth(project, cred.getUsername().get(), cred.getPassword().get(), cred.getRegistry().get());
            });
            
            String inspectedImage = DockerXCatalogUtil.inspectDockerXImage( project, catalogDigestItem.getImage() );
            
            if ( DockerXDigestUtil.isJsonValid(inspectedImage) ) {
                
                if ( DockerXDigestUtil.isJsonObject(inspectedImage) )
                    catalogDigestItem.execute( DockerXCatalogUtil.inspectDockerXImage( project, catalogDigestItem.getImage() ) );
                
                else if (DockerXDigestUtil.isJsonArray( inspectedImage ) )
                    catalogDigestItem.execute( DockerXCatalogUtil.inspectDockerXListManifest( project, catalogDigestItem.getImage() ) );
            }
        }
        
        String catalogSource = MessageFormat.format(CATALOG_SOURCE, metadataName, displayName, catalogDigestItem.getImageManifest() );
        
        if ( dryRun )
            catalogSource = catalogSource.replaceAll(".*\\R|.+\\z", Matcher.quoteReplacement("DRYRUN: ") + "$0");
        
        return catalogSource;
    }
    
}
