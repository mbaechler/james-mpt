package org.apache.james.mailbox.jcr.imap;

import org.apache.james.imap.tester.suite.Search;
import org.apache.james.mailbox.jcr.host.JCRHostSystem;

public class SearchTest extends Search{

    public SearchTest() throws Exception {
        super(JCRHostSystem.build());
    }
}
