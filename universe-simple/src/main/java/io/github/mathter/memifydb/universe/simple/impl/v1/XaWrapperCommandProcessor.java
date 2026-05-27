package io.github.mathter.memifydb.universe.simple.impl.v1;

import io.github.mathter.memifydb.command.Command;
import io.github.mathter.memifydb.command.Result;
import io.github.mathter.memifydb.command.v1.ExceptionResult;
import io.github.mathter.memifydb.command.v1.PutCommand;
import io.github.mathter.memifydb.command.v1.XaWrapperCommand;
import io.github.mathter.memifydb.universe.Context;
import io.github.mathter.memifydb.universe.simple.impl.CommandProcessor;

import javax.transaction.xa.Xid;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class XaWrapperCommandProcessor<T extends Command> implements CommandProcessor<XaWrapperCommand<T>> {
    private static final Logger LOG = Logger.getLogger(XaWrapperCommandProcessor.class.getName());

    @Override
    public Result process(Context context, XaWrapperCommand<T> command) {
        Result result;
        LOG.log(Level.FINER, "Processing command {0}", command);
        try {
            final Xid xid = command.getXid();
            final Command wrapped = command.getCommand();

            context.setXid(xid);
            result = context.getUniverse().process(context, wrapped);
        } catch (Throwable t) {
            LOG.log(
                    Level.SEVERE,
                    String.format("Error processing command: %s", command),
                    t
            );

            result = new ExceptionResult(command.getSequenceNumber(), t.toString());
        }

        return result;
    }
}
