package io.github.mathter.memifydb.common.xa;

import java.util.Arrays;

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
 * <p>
 * Simple implementation of universal xa transaction identifier {@linkplain javax.transaction.xa.Xid}.
 */
public class Xid implements javax.transaction.xa.Xid {
    private final int formatId;

    private final byte[] globalTransactionId;

    private final byte[] branchQualifier;

    protected Xid(int formatId, byte[] globalTransactionId, byte[] branchQualifier) {
        this.formatId = formatId;
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = branchQualifier;
    }

    public static Xid of(javax.transaction.xa.Xid xid) {
        return new Xid(xid.getFormatId(), xid.getGlobalTransactionId(), xid.getBranchQualifier());
    }

    public static Xid of(int formatId, byte[] globalTransactionId, byte[] branchQualifier) {
        return new Xid(formatId, globalTransactionId, branchQualifier);
    }

    @Override
    public int getFormatId() {
        return this.formatId;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return this.globalTransactionId;
    }

    @Override
    public byte[] getBranchQualifier() {
        return this.branchQualifier;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
                || (obj instanceof javax.transaction.xa.Xid another
                && this.formatId == another.getFormatId()
                && Arrays.equals(this.globalTransactionId, another.getGlobalTransactionId())
                && Arrays.equals(this.branchQualifier, another.getBranchQualifier())
        );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(branchQualifier);
        result = prime * result + formatId;
        result = prime * result + Arrays.hashCode(globalTransactionId);
        return result;
    }
}
