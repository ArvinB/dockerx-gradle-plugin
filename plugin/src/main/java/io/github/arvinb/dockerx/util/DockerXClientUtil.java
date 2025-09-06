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

import java.util.regex.*;
import com.github.dockerjava.api.*;
import com.github.dockerjava.core.*;
import com.github.dockerjava.httpclient5.*;
import com.github.dockerjava.transport.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXClientUtil {
    
    private DockerClient dockerClient = null;
    
    public boolean isDockerExperimentalOn() {
        try {
            return getDockerClient().versionCmd().exec().getExperimental();
        } catch ( Exception ex ) {
            return false;
        }
    }
    
    public boolean isDockerRunning() {
        
        try {
            getDockerClient().pingCmd().exec();
            return true;
        } catch ( Exception ex ) {
            return false;
        }
    }
    
    public String getDockerVersion() {
        
        if (isDockerRunning()) {
            return getDockerClient().versionCmd().exec().getVersion();
        }
        return null;
    }
    
    public DockerClient getDockerClient() {
        
        if ( this.dockerClient == null ) {
            this.dockerClient = defaultDockerClient();
        }
        
        return this.dockerClient;
    }
    
    public String getDockerImageId(String image) {
        
        try {
            
            return getDockerClient().inspectImageCmd(image).exec().getId();
            
        } catch (Exception ex) {
            DockerXVerifyUtil.stopGradleException(MESSAGE_DOCKER_IMAGE_ID + ex.getMessage());
        }
        
        return EMPTY_STRING;
    }
    
    ///
    
    public static String registryFromImage(String imagePath) {
        
        String registry = EMPTY_STRING;
        
        try {
            
            Pattern pattern = Pattern.compile("(?<=\\/\\/)?(.*?)(?=\\/)");
            Matcher matcher = pattern.matcher(imagePath);
            
            while (matcher.find()) {
                if ( matcher.group().trim().isEmpty() ) continue;
                if ( matcher.group().contains(":") ) continue;
                registry = matcher.group(); break;
            }
            
        } catch (Exception ex) {
            DockerXVerifyUtil.stopGradleException(MESSAGE_DOCKER_REGISTRY + ex.getMessage());
        }
        
        return registry;
    }
    
    ///
    
    protected DockerClient defaultDockerClient() {
        
        DockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
                                                                      .dockerHost( dockerClientConfig.getDockerHost() )
                                                                      .sslConfig( dockerClientConfig.getSSLConfig() )
                                                                      .build();
        
        return DockerClientImpl.getInstance( dockerClientConfig, dockerHttpClient );
    }
    
    protected DockerClient remoteDockerClient(String username, String password, String registry) {
        
        DockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                                                                         .withRegistryUsername(username)
                                                                         .withRegistryPassword(password)
                                                                         .withRegistryUrl(registry)
                                                                         .build();
        
        DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
                                                                      .dockerHost( dockerClientConfig.getDockerHost() )
                                                                      .sslConfig( dockerClientConfig.getSSLConfig() )
                                                                      .build();
        
        return DockerClientImpl.getInstance( dockerClientConfig, dockerHttpClient );
    }
    
}
