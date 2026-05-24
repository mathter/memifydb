package io.github.mathter.memifydb.universe;

import io.github.mathter.memifydb.command.Command;
import io.github.mathter.memifydb.common.data.Value;

import javax.transaction.xa.XAException;

public interface Universe {
    public Value process(Command command) throws XAException;
}
