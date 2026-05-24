package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.SequenceNumber;
import io.github.mathter.memifydb.command.xa.XaCommand;

import javax.transaction.xa.Xid;

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
public class XaEndTransactionCommand extends AbstractCommand implements XaCommand {
    private static final byte[] PREFIX = {0x0A, 0x0B};

    private final Xid xid;

    private final int flags;

    public XaEndTransactionCommand(SequenceNumber sequenceNumber, Xid xid, int flags) {
        super(sequenceNumber);
        this.xid = xid;
        this.flags = flags;
    }

    public static byte[] getPrefix() {
        return PREFIX;
    }

    @Override
    public Xid getXid() {
        return this.xid;
    }

    public int getFlags() {
        return this.flags;
    }
}
