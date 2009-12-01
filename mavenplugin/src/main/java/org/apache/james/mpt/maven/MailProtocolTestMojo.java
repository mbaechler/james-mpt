/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.mpt.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.james.mpt.ExternalHostSystem;
import org.apache.james.mpt.Monitor;
import org.apache.james.mpt.ProtocolSessionBuilder;
import org.apache.james.mpt.Runner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public class MailProtocolTestMojo extends AbstractMojo implements Monitor{

    /**
     *
     * @parameter default-value="0"
     */
    private Integer port;
    
    /**
     *
     * @parameter 
     */
    private File scriptFile;
    
    /**
     * @parameter 
     */
    private String host;

    
    /**
     * @parameter 
     */
    private String shabang;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		validate();
		
		 final ExternalHostSystem hostSystem = new ExternalHostSystem(host, port, this, shabang, null);
	     final ProtocolSessionBuilder builder = new ProtocolSessionBuilder();
	     
         final Runner runner = new Runner();
         InputStream inputStream;
		try {
			inputStream = new FileInputStream(scriptFile);
	        builder.addProtocolLines(scriptFile.getName(), inputStream, runner.getTestElements());
			runner.runSessions(hostSystem);

		} catch (IOException e1) {
            throw new MojoExecutionException("Cannot load script " + scriptFile.getName(), e1);
        } catch (Exception e) {
            throw new MojoExecutionException("[FAILURE] in script " + scriptFile.getName() + "\n" + e.getMessage(), e);
        }
       
	}

	private void validate() throws MojoFailureException {
		if (port <= 0) {
            throw new MojoFailureException("'port' configuration must be set.");
		}
		
		if (host == null) {
            throw new MojoFailureException("'host' configuration must be set.");
		}
		
		if (scriptFile == null ) {
            throw new MojoFailureException("'scriptFile' configuration must be set.");
		}
		
		if (scriptFile.exists() == false ) {
            throw new MojoFailureException("'scriptFile' not exists");
		}
		
	}
	public void debug(char character) {
		getLog().debug("'" + character + "'");
	}

	public void debug(String message) {
		getLog().debug(message);
	}

	public void note(String message) {
		getLog().debug(message);
	}
	
}
