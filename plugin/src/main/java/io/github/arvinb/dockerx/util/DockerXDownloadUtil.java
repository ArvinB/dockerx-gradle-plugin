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

import java.util.*;
import javax.annotation.*;
import org.gradle.api.*;
import io.github.arvinb.dockerx.config.*;

public class DockerXDownloadUtil {
    
    public static void downloadArtifacts(Project project, List<DockerXDownloadConfig.DownloadItem> downloadItems, @Nullable String username, @Nullable String password, boolean overwrite, boolean identityToken) {
        
        downloadItems.stream().forEach( downloadItem -> {
            downloadItem.execute(project, username, password, overwrite, identityToken);
        });
    }
}
