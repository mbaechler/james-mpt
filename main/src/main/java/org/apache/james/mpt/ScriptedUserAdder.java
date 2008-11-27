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

package org.apache.james.mpt;

import java.io.StringReader;

/**
 * Adds a user by executing a script at a port.
 * The user name and password supplied will be substituted 
 * for the variables <code>${user}</code> and <code>${password}</code>.
 */
public class ScriptedUserAdder implements UserAdder {

    private static final String SCRIPT_NAME = "Add User Script";
    private static final String PASSWORD_VARIABLE_NAME = "password";
    private static final String USER_VARIABLE_NAME = "user";
    
    private final String host;
    private final int port;
    private final String script;
    private final Monitor monitor;
    
    public ScriptedUserAdder(final String host, final int port, final String script) {
        this(host, port, script, new NullMonitor());
    }
    
    public ScriptedUserAdder(final String host, final int port, final String script, final Monitor monitor) {
        this.host = host;
        this.port = port;
        this.script = script;
        this.monitor = monitor;
    }
    
    public void addUser(final String user, final String password) throws Exception {
        final ProtocolSessionBuilder builder = new ProtocolSessionBuilder();
        builder.setVariable(USER_VARIABLE_NAME, user);
        builder.setVariable(PASSWORD_VARIABLE_NAME, password);
        
        final Runner runner = new Runner();
        final StringReader reader = new StringReader(script);
        builder.addProtocolLines(SCRIPT_NAME, reader, runner.getTestElements());
        final ExternalSessionFactory factory = new ExternalSessionFactory(host, port, monitor, null);
        runner.runSessions(factory);
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation 
     * of this object.
     */
    public String toString()
    {
        final String TAB = " ";
        
        String result = "ScriptedUserAdder ( "
            + super.toString() + TAB
            + "host = " + this.host + TAB
            + "port = " + this.port + TAB
            + "script = " + this.script + TAB
            + "monitor = " + this.monitor + TAB
            + " )";
    
        return result;
    }
    
    
}
