package org.apache.james.mpt.imapmailbox.cassandra;

import org.apache.james.mpt.api.HostSystem;
import org.apache.james.mpt.imapmailbox.cassandra.host.CassandraHostSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class CassandraMailboxTestModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public HostSystem provideHostSystem() throws Exception {
        return new CassandraHostSystem();
    }

}
