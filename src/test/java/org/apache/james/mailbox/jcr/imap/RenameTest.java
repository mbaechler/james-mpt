package org.apache.james.mailbox.jcr.imap;

import org.apache.james.imap.tester.suite.Rename;
import org.apache.james.mailbox.jcr.host.JCRHostSystem;

public class RenameTest extends Rename {

    public RenameTest() throws Exception {
        super(JCRHostSystem.build());
    }

}

