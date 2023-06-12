/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.camel;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;

public class DummyXAResource implements XAResource, Serializable {

    private final String storage;
    private static final String FILE_NAME = "crashed";

    public DummyXAResource(String storage) {
        this.storage = storage;
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        return XAResource.XA_OK;
    }

    /**
     * If the marker file does not exist, it will crash the JVM so that the transaction commit does not happen.
     *
     * @param xid A global transaction identifier
     * @param onePhase If true, the resource manager should use a one-phase
     * commit protocol to commit the work done on behalf of xid.
     * @throws XAException
     */
    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        final File markerFile = Paths.get(storage, FILE_NAME).toFile();
        if (!markerFile.exists()) {
            try {
                markerFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Runtime.getRuntime().halt(1);
        }
    }

    @Override
    public void rollback(Xid xid) throws XAException {
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        return new Xid[0];
    }

    @Override
    public boolean isSameRM(XAResource xaResource) throws XAException {
        return xaResource instanceof DummyXAResource;
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
    }

    @Override
    public void forget(Xid xid) throws XAException {
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return 0;
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        return false;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
    }
}
