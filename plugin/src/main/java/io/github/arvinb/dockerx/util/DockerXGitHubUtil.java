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

import javax.annotation.*;
import java.nio.file.*;
import org.ajoberstar.grgit.*;
import org.ajoberstar.grgit.operation.*;
import org.gradle.api.file.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXGitHubUtil {

    public static void cloneAndCheckoutRepository(@Nullable Credentials gitHubCredentials, String repoURI, DirectoryProperty cloneDir, @Nullable String repoBranch) {
        
        CloneOp gitCloneOp = new CloneOp();
        gitCloneOp.setCredentials(gitHubCredentials);
        gitCloneOp.setUri(repoURI);
        gitCloneOp.setDir(cloneDir.get().getAsFile());
        gitCloneOp.setRefToCheckout(repoBranch);
        
        try {
            
            gitCloneOp.call();
            
        } catch (Exception ex) {
            DockerXVerifyUtil.stopGradleException(MESSAGE_GITHUB_CLONE + ex.getMessage());
        }
    }

    public static void addAndCommitFile(@Nullable Credentials gitHubCredentials, DirectoryProperty cloneDir, @Nullable String repoBranch, String commitFile, String commitMessage) {

        try( Grgit grgit = Grgit.open( openOp -> { 

            openOp.setDir(cloneDir.get().getAsFile());
            openOp.setCredentials(gitHubCredentials);

            } ) ) {
                
                grgit.checkout( checkoutOp -> {
                checkoutOp.setCreateBranch(false);
                checkoutOp.setBranch(repoBranch);

            } );

            grgit.add( addOp -> addOp.setPatterns( new java.util.HashSet<>( java.util.Arrays.asList(commitFile) ) ) );
            grgit.commit( commitOp -> commitOp.setMessage(commitMessage) );
            grgit.push();
        };
    }
    
    public static String getRepositoryNameFromURI(String repositoryURI) {
        
        String repositoryName = EMPTY_STRING;
        repositoryName = Paths.get(repositoryURI).getFileName().toString();
        int index = repositoryName.lastIndexOf(".");
        repositoryName = index >= 1 ? repositoryName.substring(0, index) : repositoryName;
        return repositoryName;
    }

}
