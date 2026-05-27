package io.github.mathter.memifydb.universe.simple.impl.v1;

import io.github.mathter.memifydb.command.Result;
import io.github.mathter.memifydb.command.v1.GetCommand;
import io.github.mathter.memifydb.command.v1.RemoveCommand;
import io.github.mathter.memifydb.command.v1.ValueResult;
import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.util.Opt;
import io.github.mathter.memifydb.space.KeyValueOperations;
import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.universe.Context;
import io.github.mathter.memifydb.universe.Universe;
import io.github.mathter.memifydb.universe.simple.impl.CommandProcessor;

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
public class GetCommadProcessor implements CommandProcessor<GetCommand> {
    @Override
    public Result process(Context context, GetCommand command) {
        final String spaceName = command.getSpaceName();
        final Value key = command.getKey();

        final Xid xid = context.getXid();
        final Universe universe = context.getUniverse();
        final Space<KeyValueOperations> space = universe.getSpace(spaceName);
        final KeyValueOperations operations;

        if (xid == null) {
            operations = space.operatons();
        } else {
            operations = space.xaResource().xa(xid);
        }

        final Opt<Value> opt = operations.get(key);

        return new ValueResult(command.getSequenceNumber(), opt.orElse(null));
    }
}
