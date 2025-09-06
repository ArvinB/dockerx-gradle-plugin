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

public interface DockerXCredentialConfig {

    /**
     * A username
     */
    @Input @Optional
    public Property<String> getUsername();
    
    /**
     * A password
     */
    @Input @Optional
    public Property<String> getPassword();

    /**
     * Map of registry credentials
     * (Key = registry, Value = Map(Username,Password))
     */
    @Input @Optional
    public MapProperty<String, java.util.Map<String, String>> getRegistryCredentials();

    ///

    /**
     * Internal Class to hold a Registry Credential
     */
    public class RegistryCredential {

        private final Property<String> username;
        private final Property<String> password;
        private final Property<String> registry;

        /// Constructor

        public RegistryCredential(Property<String> username, Property<String> password, Property<String> registry) {
            this.username = username;
            this.password = password;
            this.registry = registry;
        }

        public Property<String> getUsername() { return username; }
        public Property<String> getPassword() { return password; }
        public Property<String> getRegistry() { return registry; }
    }

}
