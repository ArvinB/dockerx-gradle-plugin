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
import java.util.*;
import javax.annotation.*;
import org.gradle.api.*;
import io.github.arvinb.dockerx.config.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXExecuteUtil {
    
    public static void executeActions(Project project, List<DockerXExecuteConfig.ActionItem> actionItems) {
        
        actionItems.stream().forEach( actionItem -> {
            execAction(project, actionItem.getActionObj().get().getAsFile(), actionItem.getActionArgs().get(), actionItem.getActionWorkDir().get().getAsFile());
        });
    }
    
    ///
    
    protected static void execAction(Project project, File actionObj, List<String> actionArgs, File workingDir) {
        
        List<String> actionCommand = new ArrayList<String>(actionArgs);
        actionCommand.add(0, actionObj.toString());
        
        (new ExecuteCmdLineUtil( project, actionCommand.toArray(new String[0]) )).execute(workingDir, true);
    }
    
    ///
    
    public static class ExecuteCmdLineUtil extends DockerXCmdLineUtil {
        
        /// Constructor
        
        public ExecuteCmdLineUtil(Project project, String... args) { super( project, args ); }
        
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
                DockerXVerifyUtil.stopGradleException(MESSAGE_ACTION_EXEC + ex.getMessage());
            }
            
            return processResult;
        }
        
        public ProcessResult execute() { return execute(null, true); }
    }
}
