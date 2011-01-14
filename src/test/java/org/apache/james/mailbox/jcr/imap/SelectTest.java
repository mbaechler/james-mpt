package org.apache.james.mailbox.jcr.imap;

import org.apache.james.imap.tester.suite.Select;
import org.apache.james.mailbox.jcr.host.JCRHostSystem;

public class SelectTest extends Select{

    public SelectTest() throws Exception {
        super(JCRHostSystem.build());
    }
}
