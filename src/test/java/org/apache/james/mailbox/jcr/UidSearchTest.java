package org.apache.james.mailbox.jcr;

import org.apache.james.imap.tester.suite.UidSearch;

public class UidSearchTest extends UidSearch{

    public UidSearchTest() throws Exception {
        super(JCRHostSystem.build());
    }
}
