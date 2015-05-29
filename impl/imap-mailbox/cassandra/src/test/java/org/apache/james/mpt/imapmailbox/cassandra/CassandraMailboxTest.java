package org.apache.james.mpt.imapmailbox.cassandra;

import org.apache.james.mpt.imapmailbox.AbstractMailboxTest;
import org.apache.onami.test.annotation.GuiceModules;
import org.junit.Ignore;

@Ignore("JWC-144 Some tests are in errors")
@GuiceModules({ CassandraMailboxTestModule.class })
public class CassandraMailboxTest extends AbstractMailboxTest {

}
