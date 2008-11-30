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

package org.apache.james.mpt.ant;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.james.mpt.Monitor;
import org.apache.james.mpt.ScriptedUserAdder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;

/**
 * Task executes MPT scripts against a server
 * running on a given port and host.
 */
public class MailProtocolTestTask extends Task implements Monitor {

    private File script;
    private Union scripts;
    private int port = 0;
    private String host = "127.0.0.1";
    private boolean skip = false;
    private Collection users = new ArrayList();
    
    /**
     * Should the execution be skipped?
     * @return true if exection should be skipped, 
     * otherwise false
     */
    public boolean isSkip() {
        return skip;
    }

    /**
     * Sets execution skipping.
     * @param skip true to skip excution
     */
    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    /**
     * Gets the host (either name or number) against which this
     * test will run.
     * @return host, not null
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host (either name or number) against which this
     * test will run.
     * @param host not null
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets the port against which this test will run.
     * @return port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port aginst which this test will run.
     * @param port port number
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Gets the script to execute.
     * @return file containing test script
     */
    public File getScript() {
        return script;
    }

    /**
     * Sets the script to execute.
     * @param script not null
     */
    public void setScript(File script) {
        this.script = script;
    }

    //@Override
    public void execute() throws BuildException {
        if (port <= 0) {
            throw new BuildException("Port must be set to a positive integer");
        }
        
        if (scripts == null && script == null) {
            throw new BuildException("Scripts must be specified as an embedded resource collection"); 
        }
        
        if (scripts != null && script != null) {
            throw new BuildException("Scripts can be specified either by the script attribute or as resource collections but not both."); 
        }
        
        for(final Iterator it=users.iterator();it.hasNext();) {
            final AddUser user = (AddUser) it.next();
            user.validate();
        }
        
        if(skip) {
            log("Skipping excution");
        } else {
            doExecute();
        }
    }

    public void add(ResourceCollection resources) {
        if (scripts == null) {
            scripts = new Union();
        }
        scripts.add(resources);
    }
    
    private void doExecute() throws BuildException {
        for (final Iterator it=users.iterator();it.hasNext();) {
            final AddUser userAdder = (AddUser) it.next();
            userAdder.execute();
        }
    }
    
    public AddUser createAddUser() {
        final AddUser result = new AddUser();
        users.add(result);
        return result;
    }

    /**
     * Adds a user.
     */
    public class AddUser {
        
        private int port;
        private String user;
        private String passwd;
        private File script;
        private String scriptText;

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
        public void addText(String scriptText) {
            this.scriptText = getProject().replaceProperties(scriptText);
        }

        /**
         * Gets the file containing the user creation script.
         * @return not null
         */
        public File getScript() {
            return script;
        }

        /**
         * Sets the file containing the user creation script.
         * @param script not null
         */
        public void setScript(File script) {
            this.script = script;
        }
        
        /**
         * Validates mandatory fields have been filled.
         */
        void validate() throws BuildException {
            if (script == null && scriptText == null) {
                throw new BuildException("Either the 'script' attribute must be set, or the body must contain the text of the script");
            }
            
            if (script != null && scriptText != null) {
                throw new BuildException("Choose either script text or script attribute but not both.");
            }
            
            if (user == null) {
                throw new BuildException("'user' attribute must be set on AddUser to the name of the user to be created.");
            }
            
            if (passwd == null) {
                throw new BuildException("'passwd' attribute must be set on AddUser to the password to be set for the user created");
            }
            
            if (port <= 0) {
                throw new BuildException("'port' attribute must be set on AddUser to the port against which the script should run.");
            }
        }
        
        /**
         * Creates a user.
         * @throws BuildException
         */
        void execute() throws BuildException {
            validate();
            try {
                final File scriptFile = getScript();
                final Reader reader;
                if (scriptFile == null) {
                    reader = new StringReader(scriptText);
                } else {
                    reader = new FileReader(scriptFile);
                }
                final ScriptedUserAdder adder = new ScriptedUserAdder(getHost(), getPort(), MailProtocolTestTask.this);
                adder.addUser(getUser(), getPasswd(), reader);
            } catch (Exception e) {
                throw new BuildException("User addition failed", e);
            }
        } 
    }

    public void note(String message) {
        log(message, Project.MSG_DEBUG);
    }
}
