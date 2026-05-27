package io.github.mathter.memifydb.universe.simple.impl.v1;

import io.github.mathter.memifydb.command.Result;
import io.github.mathter.memifydb.command.v1.ExceptionResult;
import io.github.mathter.memifydb.command.v1.VoidResult;
import io.github.mathter.memifydb.command.v1.XaStartTransactionCommand;
import io.github.mathter.memifydb.universe.Context;
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
public class XaStartTransactionCommandProcessor implements CommandProcessor<XaStartTransactionCommand> {
    @Override
    public Result process(Context context, XaStartTransactionCommand command) {
        final Xid xid = command.getXid();
        final int flags = command.getFlags();

        try {
            context.getUniverse().getXAResource().start(xid, flags);
            return new VoidResult(command.getSequenceNumber());
        } catch (Throwable t) {
            return new ExceptionResult(command.getSequenceNumber(), t.toString());
        }
    }
}
