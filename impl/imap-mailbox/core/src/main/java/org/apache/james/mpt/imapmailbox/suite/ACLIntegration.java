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

package org.apache.james.mpt.imapmailbox.suite;

import org.apache.james.mailbox.model.MailboxPath;
import org.apache.james.mailbox.model.SimpleMailboxACL;
import org.apache.james.mpt.api.ImapHostSystem;
import org.apache.james.mpt.imapmailbox.GrantRightsOnHost;
import org.apache.james.mpt.imapmailbox.suite.base.BaseImapProtocol;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Locale;

public class ACLIntegration extends BaseImapProtocol {
    public static final String OTHER_USER_NAME = "Boby";
    public static final String OTHER_USER_PASSWORD = "password";
    public static final MailboxPath OTHER_USER_MAILBOX = new MailboxPath("#prinvate", OTHER_USER_NAME, "");
    @Inject
    private static ImapHostSystem system;
    @Inject
    private GrantRightsOnHost grantRightsOnHost;

    public ACLIntegration() throws Exception {
        super(system);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        system.addUser(OTHER_USER_NAME, OTHER_USER_PASSWORD);
    }

    @Test
    public void rightRShouldBeSufficientToPerformStatusSelectCloseExamineUS() throws Exception {
        grantRightsOnHost.grantRights(OTHER_USER_MAILBOX, USER, new SimpleMailboxACL.Rfc4314Rights("r"));
        scriptTest("aclIntegration/ACLIntegrationRightR", Locale.US);
    }

    @Test
    public void rightRShouldBeNeededToPerformStatusSelectCloseExamineUS() throws Exception {
        grantRightsOnHost.grantRights(OTHER_USER_MAILBOX, USER, new SimpleMailboxACL.Rfc4314Rights("lswipkxtecda"));
        scriptTest("aclIntegration/ACLIntegrationWithoutRightR", Locale.US);
    }

    @Test
    public void rightLShouldBeSufficientToPerformListUS() throws Exception {
        grantRightsOnHost.grantRights(USER, OTHER_USER_MAILBOX, "l");
        scriptTest("aclIntegration/ACLIntegrationRightL", Locale.US);
    }

    @Test
    public void rightLShouldBeNeededToPerformListLsubSubscribeUS() throws Exception {
        grantRightsOnHost.grantRights(USER, OTHER_USER_MAILBOX, "rswipkxtecda");
        scriptTest("aclIntegration/ACLIntegrationWithoutRightL", Locale.US);
    }

    @Test
    public void rightAShouldBeSufficientToManageACLUS() throws Exception {
        grantRightsOnHost.grantRights(OTHER_USER_MAILBOX, USER, new SimpleMailboxACL.Rfc4314Rights("a"));
        scriptTest("aclIntegration/ACLIntegrationRightA", Locale.US);
    }

    @Test
    public void rightAShouldBeNeededToManageACLUS() throws Exception {
        grantRightsOnHost.grantRights(OTHER_USER_MAILBOX, USER, new SimpleMailboxACL.Rfc4314Rights("rswipkxtecdl"));
        scriptTest("aclIntegration/ACLIntegrationWithoutRightA", Locale.US);
    }

}
