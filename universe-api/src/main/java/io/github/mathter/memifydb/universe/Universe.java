package io.github.mathter.memifydb.universe;

import io.github.mathter.memifydb.common.command.Command;
import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.space.Space;

import java.util.Collection;

public interface Universe {
    public Collection<Space> spaces();

    public Value process(Command command);
}
