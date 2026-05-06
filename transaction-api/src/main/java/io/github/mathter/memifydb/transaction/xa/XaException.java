package io.github.mathter.memifydb.transaction.xa;

import javax.transaction.xa.XAException;

public class XaException extends XAException {
    public XaException() {
    }

    public XaException(String message, int errcode) {
        super(message);
        this.errorCode = errcode;
    }
}
