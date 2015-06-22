package org.apache.james.mpt.imapmailbox.cyrus;

import org.apache.onami.test.OnamiSuite;
import org.apache.onami.test.annotation.GuiceModules;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@Ignore("JWC-156 create mailbox in Cyrus")
@RunWith(OnamiSuite.class)
@SuiteClasses({ 
//    AuthenticatedState.class,
//    ConcurrentSessions.class,
//    Events.class,
//    Expunge.class,
//    Fetch.class,
//    FetchBodySection.class,
//    FetchBodyStructure.class,
//    FetchHeaders.class,
//    Listing.class,
//    NonAuthenticatedState.class,
//    PartialFetch.class,
//    Rename.class,
//    Search.class,
//    Security.class,
//    Select.class,
//    SelectedInbox.class,
//    SelectedState.class,
//    UidSearch.class
})
@GuiceModules({CyrusMailboxTestModule.class})
public class CyrusMailboxTest {
}
