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
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.james.mpt.ExternalHostSystem;
import org.apache.james.mpt.Monitor;
import org.apache.james.mpt.ProtocolSessionBuilder;
import org.apache.james.mpt.Runner;
import org.apache.james.mpt.ScriptedUserAdder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal run
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
    private String shabang;
    
    /**
     * @parameter
     */
    private AddUser[] addUsers;


    /**
     * Gets the host (either name or number) against which this
     * test will run.
     * @return host, not null
     */
    public String getHost() {
        return host;
    }


    /**
     * Gets the port against which this test will run.
     * @return port number
     */
    public int getPort() {
        return port;
    }

    
    /*
     * (non-Javadoc)
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
	public void execute() throws MojoExecutionException, MojoFailureException {
		validate();
		
     
		for (int i = 0; i < addUsers.length; i++) {
			AddUser addUser = (AddUser) addUsers[i];
			  try {
		            
		            final Reader reader = new StringReader(addUser.getText());
		            
		            final ScriptedUserAdder adder = new ScriptedUserAdder(addUser.getHost(), addUser.getPort(), MailProtocolTestMojo.this);
		            adder.addUser(addUser.getUser(), addUser.getPasswd(), reader);
		        } catch (Exception e) {
		            getLog().error("Unable to add user", e);
		            throw new MojoFailureException("User addition failed: \n" + e.getMessage());
		        }
		}
        final Runner runner = new Runner();
        InputStream inputStream;
		try {
			inputStream = new FileInputStream(scriptFile);

			final ExternalHostSystem hostSystem = new ExternalHostSystem(host, port, this, shabang, null);
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
		
		for (int i = 0; i < addUsers.length; i++) {
			AddUser addUser = (AddUser)addUsers[i];
			
			if (addUser.getText() == null) {
	            throw new MojoFailureException("AddUser must contain the text of the script");
	        }
	        
	        if (addUser.getPort() <= 0) {
	            throw new MojoFailureException("'port' attribute must be set on AddUser to the port against which the script should run.");
	        }
	        
	        if (addUser.getHost() == null) {
	            throw new MojoFailureException("'host' attribute must be set on AddUser to the host against which the script should run.");
	        }
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
	
 
	
    /**
     * Adds a user.
     */
    public class AddUser {
        
        private int port;
        private String user;
        private String passwd;
        private String scriptText;
        private String host;
        
        /**
         * Gets the host (either name or number) against which this
         * test will run.
         * @return host, not null
         */
        public String getHost() {
            return host;
        }

        public void setHost(String host) {
        	this.host = host;
        }
        
        /**
         * Gets the port against which the user addition
         * script should be executed.
         * @return port number
         */
        public int getPort() {
            return port;
        }

        /**
         * Sets the port against which the user addition
         * script should be executed.
         * @param port port number
         */
        public void setPort(int port) {
            this.port = port;
        }

        /**
         * Gets the password for the user.
         * @return password not null
         */
        public String getPasswd() {
            return passwd;
        }

        /**
         * Sets the password for the user.
         * This will be passed in the user creation script.
         * @param passwd not null
         */
        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        /**
         * Gets the name of the user to be created.
         * @return user name, not null
         */
        public String getUser() {
            return user;
        }

        /**
         * Sets the name of the user to be created.
         * @param user not null
         */
        public void setUser(String user) {
            this.user = user;
        }
        
        /**
         * Sets user addition script.
         * @param scriptText not null
         */
        public void setText(String scriptText) {
            this.scriptText = scriptText;
        }


        public String getText() {
            return scriptText;
        }

        
    }
    
}
