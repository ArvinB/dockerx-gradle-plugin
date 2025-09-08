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

public interface DockerXExecuteConfig {
    
    /**
     * Hide args (Default: true)
     */
    @Input @Optional
    public Property<Boolean> getHideArgs();

    /**
     * Map of actions (relative directory + script, arguments)
     */
    @Input
    public MapProperty<String, String> getActions();
    
    /**
     * Internal Class to hold an Action Item
     */
    public class ActionItem {
        
        private DirectoryProperty actionWorkDir;
        private RegularFileProperty actionObj;
        private ListProperty<String> actionArgs;
        
        public ActionItem(DirectoryProperty actionWorkDir, RegularFileProperty actionObj, ListProperty<String> actionArgs) throws GradleException {
            
            try {
                
                this.actionWorkDir = actionWorkDir;
                this.actionObj = actionObj;
                this.actionArgs = actionArgs;
            
            } catch (Exception ex) {
                DockerXVerifyUtil.stopGradleException(MESSAGE_ACTION_ITEM + ex.getMessage());
            }
        }
        
        ///
        
        @Input
        public DirectoryProperty getActionWorkDir() { return actionWorkDir; }
        
        @Input
        public RegularFileProperty getActionObj() { return actionObj; }
        
        @Input
        public ListProperty<String> getActionArgs() { return actionArgs; }
        
        @Override
        public String toString() {
            return this.actionObj.getAsFile().get().getAbsolutePath() + ", " + this.actionArgs.get().toString();
        }
    }
}