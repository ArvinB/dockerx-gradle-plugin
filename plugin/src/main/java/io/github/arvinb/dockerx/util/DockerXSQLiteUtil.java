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
import java.util.stream.*;
import javax.annotation.*;
import org.gradle.api.*;
import org.gradle.api.file.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public class DockerXSQLiteUtil {
    
    public static String getSQLiteVersion(Project project) {
        
        String sqliteVersion = EMPTY_STRING;
        
        DockerXCmdLineUtil.ProcessResult processResult = (new SQLiteCmdLineUtil(project, CMD_LINE_SQLITE, CMD_LINE_OPT_VERSION)).execute();
        
        if (processResult.exitCode == 0) {
            
            Optional<String> firstLine = processResult.getOutputLines().stream().findFirst();
            
            if ( firstLine.isPresent() ) {
                
                List<String> versionList = Stream.of( firstLine.get().split(" ") ).map(String::trim).collect(Collectors.toList() );
                
                if (!versionList.isEmpty())
                    sqliteVersion = versionList.get(0);
            }
        }
        
        return sqliteVersion;
    }
    
    public static void executeSQLiteStatements(Project project, 
                                               RegularFile database, 
                                               List<String> statements) {
        
        if ( statements.isEmpty() || database == null ) return; // Nothing to do...
        
        try {
            
            File databaseFile = database.getAsFile();
            
            if ( databaseFile.exists() && databaseFile.isFile() )
                statements.forEach( statement -> {
                    execSQLiteStatement(project, databaseFile.getPath(), statement);
                });
            
        } catch (Exception ex) {
            DockerXVerifyUtil.stopGradleException(MESSAGE_SQLITE_STMT_EXEC + ex.getMessage());
        }
    }
    
    ///
    
    protected static void execSQLiteStatement(Project project, String datasource, String statement) {
        
        (new SQLiteCmdLineUtil(project, CMD_LINE_SQLITE, datasource, statement)).execute();
    }
    
    ///
    
    public static class SQLiteCmdLineUtil extends DockerXCmdLineUtil {
        
        /// Constructor
        
        public SQLiteCmdLineUtil(Project project, String... args) { super( project, args ); }
        
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
                DockerXVerifyUtil.stopGradleException(MESSAGE_SQLITE_EXEC + ex.getMessage());
            }
            
            return processResult;
        }
        
        public ProcessResult execute() { return execute(null, true); }
    }
}
