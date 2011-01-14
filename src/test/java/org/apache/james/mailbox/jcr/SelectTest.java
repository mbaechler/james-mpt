package org.apache.james.mailbox.jcr;

import org.apache.james.imap.tester.suite.Select;

public class SelectTest extends Select{

    public SelectTest() throws Exception {
        super(JCRHostSystem.build());
    }
}
