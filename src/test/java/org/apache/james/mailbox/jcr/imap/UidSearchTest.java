package org.apache.james.mailbox.jcr.imap;

import org.apache.james.imap.tester.suite.UidSearch;
import org.apache.james.mailbox.jcr.host.JCRHostSystem;

public class UidSearchTest extends UidSearch{

    public UidSearchTest() throws Exception {
        super(JCRHostSystem.build());
    }
}
