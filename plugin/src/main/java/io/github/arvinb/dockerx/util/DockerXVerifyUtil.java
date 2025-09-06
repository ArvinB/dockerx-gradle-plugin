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

import org.gradle.api.*;
import org.gradle.internal.os.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXVerifyUtil {
    
    public static void verifyDockerEnvironment() {
        
        DockerXClientUtil dockerXClientUtil = new DockerXClientUtil();
        
        // Check for Docker is Running
        if (!dockerXClientUtil.isDockerRunning()) {
            stopGradleException(VERIFY_DOCKER_RUNNING);
        }
            
        // Check the Docker Version
        if (compareVersion(dockerXClientUtil.getDockerVersion(), VERIFY_MIN_DOCKER_VER) < 0) {
            stopGradleException(VERIFY_DOCKER_VERSION);
        }
    }
    
    public static void verifyGitHubRepo(String gitHubRepo) {
        
        // Check for GitHub Repo URI
        if ( (gitHubRepo == null) || ( gitHubRepo.trim().isEmpty() ) || ( (!gitHubRepo.startsWith(VERIFY_GITHUB_GIT)) && (!gitHubRepo.startsWith(VERIFY_GITHUB_HTTP)) ) ) {
            stopGradleException(VERIFY_GITHUB_REPO_URI);
        }
    }
    
    public static void verifyOperatorPackageManager(Project project) {
        
        // Check the OPM Version
        if (compareVersion(DockerXOPMUtil.getOPMVersion(project), VERIFY_MIN_OPM_VER) < 0) {
            stopGradleException(VERIFY_OPM_VERSION);
        }
    }
    
    public static void verifySQLite(Project project) {
        
        // Check the SQLite Version
        if (compareVersion(DockerXSQLiteUtil.getSQLiteVersion(project), VERIFY_MIN_SQLITE_VER) < 0) {
            stopGradleException(VERIFY_SQLITE_VERSION);
        }
    }
    
    public static void stopGradleException(String message) {
        throw new GradleException(message);
    }
    
    public static boolean isCurrentOS(DockerXVerifyUtil.OS os) {
        
        switch (os) {
            case UNIX:
                return OperatingSystem.current().isUnix();
            case LINUX:
                return OperatingSystem.current().isLinux();
            case MAC:
                return OperatingSystem.current().isMacOsX();
            case WINDOWS:
                return OperatingSystem.current().isWindows();
            default:
                return false;
        }
    }
    
    ///
    
    private static int compareVersion(String versionA, String versionB) {
        
        // Return < 0 When Version A < Version B
        // Return  0  When Version A = Version B
        // Return > 0 When Version A > Version B
        
        try {
            
            String[] firstVersion = versionA.split("\\.");
            String[] secondVersion = versionB.split("\\.");
            
            int length = Math.max(firstVersion.length, secondVersion.length);
            
            for ( int i = 0; i < length; i++ ) {
                
                Integer firstVersionInt  = i < firstVersion.length  ? Integer.parseInt(firstVersion[i])  : 0;
                Integer secondVersionInt = i < secondVersion.length ? Integer.parseInt(secondVersion[i]) : 0;
                int versionComparison = firstVersionInt.compareTo(secondVersionInt);
                
                if ( versionComparison != 0 )
                    return versionComparison;
            }
            
        } catch ( Exception ex ) { stopGradleException(MESSAGE_VERSION_COMPARISON); }
        
        return 0;
    }
    
    ///
    
    public enum OS {
        UNIX, LINUX, MAC, WINDOWS
    };
}
