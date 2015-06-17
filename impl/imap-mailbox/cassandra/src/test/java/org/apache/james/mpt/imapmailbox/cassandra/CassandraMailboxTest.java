package org.apache.james.mpt.imapmailbox.cassandra;

import org.apache.james.mpt.imapmailbox.AbstractMailboxTest;
import org.apache.onami.test.annotation.GuiceModules;

@GuiceModules({ CassandraMailboxTestModule.class })
public class CassandraMailboxTest extends AbstractMailboxTest {

}
