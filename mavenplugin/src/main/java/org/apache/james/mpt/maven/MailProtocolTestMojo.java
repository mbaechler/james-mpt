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
import java.io.InputStreamReader;

import org.apache.james.mpt.ExternalHostSystem;
import org.apache.james.mpt.Monitor;
import org.apache.james.mpt.ProtocolSessionBuilder;
import org.apache.james.mpt.Runner;
import org.apache.james.mpt.ScriptedUserAdder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal mpt
 */
public class MailProtocolTestMojo extends AbstractMojo implements Monitor{

    /**
     *
     * @parameter
     * @required
     */
    private Integer port;
    
    /**
     *
     * @parameter 
     * @required
     */
    private File scriptFile;
    
    /**
     * @required
     * @parameter 
     */
    private String host;

    /**
     * @parameter 
     */
    private String user;
    /**
     * @parameter 
     */
    private String pass;
    /**
     * @parameter 
     */
    private String shabang;
    
    /*
     * (non-Javadoc)
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
	public void execute() throws MojoExecutionException, MojoFailureException {
		validate();
		
     
         final Runner runner = new Runner();
         InputStream inputStream;
		try {
			inputStream = new FileInputStream(scriptFile);

		    final ScriptedUserAdder adder = new ScriptedUserAdder(host, port,this);
	        
			if (user != null) adder.addUser(user, pass, new InputStreamReader(inputStream));
	        
			final ExternalHostSystem hostSystem = new ExternalHostSystem(host, port, this, shabang, adder);
		    final ProtocolSessionBuilder builder = new ProtocolSessionBuilder();
		     
	        builder.addProtocolLines(scriptFile.getName(), inputStream, runner.getTestElements());
			runner.runSessions(hostSystem);

		} catch (IOException e1) {
            throw new MojoExecutionException("Cannot load script " + scriptFile.getName(), e1);
        } catch (Exception e) {
            throw new MojoExecutionException("[FAILURE] in script " + scriptFile.getName() + "\n" + e.getMessage(), e);
        }
       
	}

	/**
	 * Validate if the configured parameters are valid
	 * 
	 * @throws MojoFailureException
	 */
	private void validate() throws MojoFailureException {
		if (port <= 0) {
            throw new MojoFailureException("'port' configuration must be set.");
		}
		
		if (scriptFile.exists() == false ) {
            throw new MojoFailureException("'scriptFile' not exists");
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.apache.james.mpt.Monitor#debug(char)
	 */
	public void debug(char character) {
		getLog().debug("'" + character + "'");
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.james.mpt.Monitor#debug(java.lang.String)
	 */
	public void debug(String message) {
		getLog().debug(message);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.james.mpt.Monitor#note(java.lang.String)
	 */
	public void note(String message) {
		getLog().debug(message);
	}
	
}
