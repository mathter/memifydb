package io.github.mathter.memifydb.space.simple;

import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.data.ValueFactory;
import io.github.mathter.memifydb.common.data.ValueTranslator;
import io.github.mathter.memifydb.common.data.fasterxml.FasterXmlValueFactory;
import io.github.mathter.memifydb.common.util.Opt;
import io.github.mathter.memifydb.space.KeyValueOperations;
import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.space.SpaceFactory;
import io.github.mathter.memifydb.transaction.xa.XaResourceProvider;
import io.github.mathter.memifydb.transaction.xa.Xid;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.transaction.xa.XAResource;
import java.nio.charset.StandardCharsets;

/**
 * Copyright 2026 Alexander Kashirsky (mathter)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class XaSimpleSpaceTest {
    private final ValueFactory valueFactory = ValueFactory.get(FasterXmlValueFactory.ID);

    private final ValueTranslator translator = this.valueFactory.translator();

    private Space<KeyValueOperations> space;

    @BeforeEach
    public void init() {
        this.space = SpaceFactory.getInstance(Const.ID)
                .get(RandomStringUtils.random(10));
    }

    @Test
    public void testUseOperationsNoValidResource() throws Exception {
        final Xid xid = Xid.of(0, RandomStringUtils.random(5).getBytes(StandardCharsets.UTF_8), RandomStringUtils.random(5).getBytes(StandardCharsets.UTF_8));
        final XaResourceProvider<KeyValueOperations> xaResource = this.space.xaResource();

        final KeyValueOperations operations = xaResource.xa(xid);
        Assertions.assertThrows(IllegalStateException.class, () -> operations.put(this.translator.from(10), this.translator.from(5)));

        xaResource.start(xid, XAResource.TMNOFLAGS);
        xaResource.end(xid, XAResource.TMSUCCESS);
        Assertions.assertThrows(IllegalStateException.class, () -> operations.put(this.translator.from(10), this.translator.from(5)));
    }

    @Test
    public void testCommitTwoPhase() throws Exception {
        final Value key = this.translator.from(RandomStringUtils.random(10));
        final Value value = this.translator.from(RandomStringUtils.random(10));
        final Xid xid = Xid.of(0, RandomStringUtils.random(5).getBytes(StandardCharsets.UTF_8), RandomStringUtils.random(5).getBytes(StandardCharsets.UTF_8));
        final XaResourceProvider<KeyValueOperations> xaResource = this.space.xaResource();

        xaResource.start(xid, XAResource.TMNOFLAGS);
        final KeyValueOperations operations = xaResource.xa(xid);
        operations.put(key, value);

        Assertions.assertTrue(this.space.operatons().get(key).isEmpty());
        xaResource.end(xid, XAResource.TMSUCCESS);
        xaResource.prepare(xid);
        xaResource.commit(xid, false);
        final Opt<Value> opt = this.space.operatons().get(key);
        Assertions.assertNotNull(opt);
        Assertions.assertTrue(opt.isPresent());
        Assertions.assertEquals(value, opt.get());
    }

    @Test
    public void testCommitOnePhase() throws Exception {
        final Value key = this.translator.from(RandomStringUtils.random(10));
        final Value value = this.translator.from(RandomStringUtils.random(10));
        final Xid xid = Xid.of(0, RandomStringUtils.random(5).getBytes(StandardCharsets.UTF_8), RandomStringUtils.random(5).getBytes(StandardCharsets.UTF_8));
        final XaResourceProvider<KeyValueOperations> xaResource = this.space.xaResource();

        xaResource.start(xid, XAResource.TMNOFLAGS);
        final KeyValueOperations operations = xaResource.xa(xid);
        operations.put(key, value);

        Assertions.assertTrue(this.space.operatons().get(key).isEmpty());
        xaResource.end(xid, XAResource.TMSUCCESS);
        xaResource.commit(xid, true);
        final Opt<Value> opt = this.space.operatons().get(key);
        Assertions.assertNotNull(opt);
        Assertions.assertTrue(opt.isPresent());
        Assertions.assertEquals(value, opt.get());
    }

    @Test
    public void testRollback() throws Exception {
        final Value key = this.translator.from(RandomStringUtils.random(10));
        final Value value = this.translator.from(RandomStringUtils.random(10));
        final Xid xid = Xid.of(0, RandomStringUtils.random(5).getBytes(StandardCharsets.UTF_8), RandomStringUtils.random(5).getBytes(StandardCharsets.UTF_8));
        final XaResourceProvider<KeyValueOperations> xaResource = this.space.xaResource();

        xaResource.start(xid, XAResource.TMNOFLAGS);
        final KeyValueOperations operations = xaResource.xa(xid);
        operations.put(key, value);

        Assertions.assertTrue(this.space.operatons().get(key).isEmpty());
        xaResource.end(xid, XAResource.TMSUCCESS);
        xaResource.prepare(xid);
        xaResource.rollback(xid);
        final Opt<Value> opt = this.space.operatons().get(key);
        Assertions.assertNotNull(opt);
        Assertions.assertTrue(opt.isEmpty());
    }
}
