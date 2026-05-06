package io.github.mathter.memifydb.transaction.xa;

import java.util.Arrays;

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
