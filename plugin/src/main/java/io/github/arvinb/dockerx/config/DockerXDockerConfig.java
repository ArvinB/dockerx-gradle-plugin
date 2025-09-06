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

import org.gradle.api.provider.*;
import org.gradle.api.tasks.*;

public interface DockerXDockerConfig {
    
    /**
     * A devbuild flag (Default: false)
     */
    @Input @Optional
    public Property<Boolean> getDevbuild();

    /**
     * A multiArchReset flag (Default: false)
     */
    @Input @Optional
    public Property<Boolean> getMultiArchReset();
    
    /**
     * A clean flag (Default: true)
     */
    @Input @Optional
    public Property<Boolean> getClean();
    
    /**
     * A push flag (Default: false)
     */
    @Input @Optional
    public Property<Boolean> getPush();
    
    /**
     * A name for the docker builder
     */
    @Input @Optional
    public Property<String> getBuilder();

    /**
     * A value for the docker provenance (min (default), max, false)
     */
    @Input @Optional
    public Property<String> getProvenance();
    
    /**
     * A docker target
     */
    @Input @Optional
    public Property<String> getTarget();
    
    /**
     * The Dockerfile build configuration (Overloaded property)
     * A relative path to the derived working directory
     */
    @Input @Optional
    public Property<String> getDockerfile();
    
    /**
     * The docker build arguments
     */
    @Input @Optional
    public MapProperty<String, String> getBuildArgs();
    
    /**
     * The docker secrets.
     * Source secret file is a relative path to project directory
     */
    @Input @Optional
    public MapProperty<String, String> getSecrets();
    
    /**
     * List of docker platforms
     */
    @Input @Optional
    public ListProperty<String> getPlatforms();
    
    /**
     * List of docker images
     */
    @Input @Optional
    public ListProperty<String> getImages();
    
    ///
    
    public enum PlatformItem {
        
        AMD64("linux/amd64"),
        ARM64("linux/arm64"),
        RISCV64("linux/riscv64"),
        PPC64LE("linux/ppc64le"),
        S390X("linux/s390x"),
        X386("linux/386"),
        ARMV7("linux/arm/v7"),
        ARMV6("linux/arm/v6");

        private String platform;
        
        PlatformItem(String platform) {
            this.platform = platform;
        }
        
        public String shortString() {
            
            switch (this) {
            case AMD64:
                return "amd64";
            case ARM64:
                return "arm64";
            case ARMV6:
                return "arm_v6";
            case ARMV7:
                return "arm_v7";
            case PPC64LE:
                return "ppc64le";
            case RISCV64:
                return "riscv64";
            case S390X:
                return "s390x";
            case X386:
                return "386";
            }
            
            return "";
        }
        
        public static PlatformItem fromString(String platform) {
            
            for (PlatformItem dockerXPlatform : PlatformItem.values()) {
                
                if (dockerXPlatform.platform.equals(platform))
                    return dockerXPlatform;
            }
            return null;
        }
        
        public static String commaSeparatedList(java.util.List<PlatformItem> platforms) {
            
            return platforms.stream().map(platform -> String.valueOf(platform.toString())).collect(java.util.stream.Collectors.joining(","));
        }
        
        @Override
        public String toString() {
            return this.platform;
        }

    }
}
