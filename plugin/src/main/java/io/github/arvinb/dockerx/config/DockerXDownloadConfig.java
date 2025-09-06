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

import javax.annotation.*;
import org.gradle.api.*;
import org.gradle.api.file.*;
import org.gradle.api.provider.*;
import org.gradle.api.tasks.*;
import io.github.arvinb.dockerx.util.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public interface DockerXDownloadConfig {

    /**
     * A download overwrite flag (Default: false)
     */
    @Input @Optional
    public Property<Boolean> getOverwrite();
    
    /**
     * A download overwrite flag (Default: false)
     */
    @Input @Optional
    public Property<Boolean> getIdentityToken();

    /**
     * Map of downloads (relative directory, url)
     */
    @Input
    public MapProperty<String, String> getDownloads();
    
    /**
     * Internal Class to hold a Download Item
     */
    public class DownloadItem {
        
        private DirectoryProperty downloadDir;
        private java.net.URL downloadURL;
        
        public DownloadItem(DirectoryProperty downloadDir, String downloadURL) throws GradleException {
            
            try {
                
                this.downloadDir = downloadDir;
                this.downloadURL = java.nio.file.Paths.get(downloadURL).toUri().toURL();
                
            } catch (Exception ex) {
                DockerXVerifyUtil.stopGradleException(MESSAGE_DOWNLOAD_URL + ex.getMessage());
            }
        }
        
        public void execute(Project project, @Nullable String username, @Nullable String password) {
            execute(project, username, password, false, false);
        }
        
        public void execute(Project project, @Nullable String username, @Nullable String password, boolean overwriteFlag, boolean useIdentityToken) throws GradleException {
            
            try {
                
                java.io.File workingDir = (DownloadItem.isPathDirectory(downloadDir.get().getAsFile())) ? downloadDir.get().getAsFile() : downloadDir.get().getAsFile().getParentFile();
                workingDir.mkdirs();
                
                de.undercouch.gradle.tasks.download.DownloadAction downloadAction = 
                new de.undercouch.gradle.tasks.download.DownloadAction( project );
                
                downloadAction.src(downloadURL.toExternalForm());
                downloadAction.dest(downloadDir.get().getAsFile().getPath());
                downloadAction.acceptAnyCertificate(true);
                downloadAction.overwrite(overwriteFlag);

                if (useIdentityToken) {

                    if (password != null)
                        downloadAction.header(AUTHORIZATION, BEARER + " " + password);
                
                } else {

                    if ( (username != null) && (password != null) ) {
                        if (!username.trim().isEmpty()) downloadAction.username(username);
                        if (!password.trim().isEmpty()) downloadAction.password(password);
                    }
                }
                
                downloadAction.execute();
                
            } catch (Exception ex) {
                DockerXVerifyUtil.stopGradleException(MESSAGE_DOWNLOAD_EXEC + ex.getMessage());
            }
        }
        
        ///
        
        @Input
        public DirectoryProperty getDownloadDir() { return downloadDir; }
        
        @Input
        public java.net.URL getDownloadURL() { return downloadURL; }
        
        @Override
        public String toString() {
            return this.downloadURL.toExternalForm() + ", " + this.downloadDir.get().getAsFile().getPath();
        }
        
        ///
        
        public static boolean isPathDirectory(java.io.File file) {
            
            if (!file.exists()) {
                return file.getName().lastIndexOf('.') == -1;
            } else {
                return file.isDirectory();
            }
        }
    }
}
