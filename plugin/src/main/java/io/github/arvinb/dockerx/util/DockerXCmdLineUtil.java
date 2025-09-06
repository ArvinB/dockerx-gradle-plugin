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
import static io.github.arvinb.dockerx.DockerXConstants.*;

public abstract class DockerXCmdLineUtil {
    
    private final List<String> args = new ArrayList<String>();
    private final Project project ;
    
    /// Constructors
    
    public DockerXCmdLineUtil(Project project, String... args) {
        this.project = project;
        this.args.addAll( Arrays.asList(args) );
    }
    
    ///
    
    public ProcessResult execute(@Nullable File workingDir, boolean captureOutput) throws Exception {
        
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(this.args);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(workingDir);
        processBuilder.environment().putAll(System.getenv());
        
        Process process = processBuilder.start();
        
        String streamOutput = processOutput(process.getInputStream(), captureOutput);
        int exitCode = process.waitFor();
        
        return new ProcessResult(exitCode, streamOutput);
    }
    
    @Override
    public String toString() {
        return String.join(", ", this.args);
    }
    
    ///
    
    private String processOutput(InputStream inputStream, boolean captureOutput) throws Exception {
        
        StringBuilder stringBuilder = new StringBuilder(EMPTY_STRING);
        BufferedReader bufferReader = new BufferedReader( new InputStreamReader(inputStream) );
        
        bufferReader.lines().forEach( line -> {
            
            project.getLogger().lifecycle(line);
            
            if (captureOutput)
                stringBuilder.append(line + NEW_LINE);
        });
        
        bufferReader.close();
        return stringBuilder.toString();
    }
    
    ///
    
    public class ProcessResult {
        
        public final int exitCode;
        public final String cmdOutput;
        
        public ProcessResult(int exitCode, String cmdOutput) {
            this.exitCode = exitCode;
            this.cmdOutput = cmdOutput;
        }
        
        public List<String> getOutputLines() {
            return Arrays.asList( this.cmdOutput.split(NEW_LINE) );
        }
    }

}
