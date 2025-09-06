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

package io.github.arvinb.dockerx;

import org.gradle.api.*;
import io.github.arvinb.dockerx.task.*;

import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXPlugin implements Plugin<Project> {

    public void apply(Project project) {
        
        project.getExtensions().create(EXTENSION_NAME, DockerXExtension.class, project.getObjects(), project.getLayout());

        project.getTasks().register(TASK_GITHUB,         DockerXGitHub.class       ).configure( publish -> { publish.setGroup(TASK_GROUP); });
        project.getTasks().register(TASK_DOWNLOAD,       DockerXDownload.class     ).configure( publish -> { publish.setGroup(TASK_GROUP); });
        project.getTasks().register(TASK_BUILD,          DockerXBuild.class        ).configure( publish -> { publish.setGroup(TASK_GROUP); });
        project.getTasks().register(TASK_CATALOG,        DockerXCatalog.class      ).configure( publish -> { publish.setGroup(TASK_GROUP); });
        project.getTasks().register(TASK_DIGEST,         DockerXDigest.class       ).configure( publish -> { publish.setGroup(TASK_GROUP); });
        project.getTasks().register(TASK_EXECUTE,        DockerXExecute.class      ).configure( publish -> { publish.setGroup(TASK_GROUP); });
        project.getTasks().register(TASK_OPM_BUNDLE,     DockerXOPMBundle.class    ).configure( publish -> { publish.setGroup(TASK_GROUP); });
        project.getTasks().register(TASK_OPM_INIT,       DockerXOPMInit.class      ).configure( publish -> { publish.setGroup(TASK_GROUP); });
        project.getTasks().register(TASK_OPM_REGISTRY,   DockerXOPMRegistry.class  ).configure( publish -> { publish.setGroup(TASK_GROUP); });
        project.getTasks().register(TASK_OPM_RENDER,     DockerXOPMRender.class    ).configure( publish -> { publish.setGroup(TASK_GROUP); });
        project.getTasks().register(TASK_OPM_VALIDATE,   DockerXOPMValidate.class  ).configure( publish -> { publish.setGroup(TASK_GROUP); });
        project.getTasks().register(TASK_SQLITE_EXECUTE, DockerXSQLiteExecute.class).configure( publish -> { publish.setGroup(TASK_GROUP); });
    }
}
