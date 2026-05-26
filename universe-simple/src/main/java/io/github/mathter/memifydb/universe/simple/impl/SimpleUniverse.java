package io.github.mathter.memifydb.universe.simple.impl;

import io.github.mathter.memifydb.command.Command;
import io.github.mathter.memifydb.command.Result;
import io.github.mathter.memifydb.command.v1.PutCommand;
import io.github.mathter.memifydb.command.v1.RemoveCommand;
import io.github.mathter.memifydb.command.v1.XaCommitTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaEndTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaPrepareTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaRollbackTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaStartTransactionCommand;
import io.github.mathter.memifydb.command.xa.XaCommand;
import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.data.ValueDeserializer;
import io.github.mathter.memifydb.common.data.ValueFactory;
import io.github.mathter.memifydb.common.data.ValueSerializer;
import io.github.mathter.memifydb.common.data.ValueTranslator;
import io.github.mathter.memifydb.space.Operations;
import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.universe.Context;
import io.github.mathter.memifydb.universe.Universe;
import io.github.mathter.memifydb.universe.simple.v1.PutCommadProcessor;
import io.github.mathter.memifydb.universe.simple.v1.RemoveCommadProcessor;
import io.github.mathter.memifydb.universe.simple.v1.XaCommitTransactionCommandProcessor;
import io.github.mathter.memifydb.universe.simple.v1.XaEndTransactionCommandProcessor;
import io.github.mathter.memifydb.universe.simple.v1.XaPrepareTransactionCommandProcessor;
import io.github.mathter.memifydb.universe.simple.v1.XaRollbackTransactionCommandProcessor;
import io.github.mathter.memifydb.universe.simple.v1.XaStartTransactionCommandProcessor;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
class SimpleUniverse implements Universe {
    private final UUID id;

    private final Map<String, Space<?>> spaces;

    private final ValueSerializer valueSerializer;

    private final ValueDeserializer valueDeserializer;

    private final ValueTranslator valueTranslator;

    private final PutCommadProcessor putCommadProcessor = new PutCommadProcessor();

    private final RemoveCommadProcessor removeCommadProcessor = new RemoveCommadProcessor();

    private final XaStartTransactionCommandProcessor xaStartTransactionCommandProcessor = new XaStartTransactionCommandProcessor();

    private final XaEndTransactionCommandProcessor xaEndTransactionCommandProcessor = new XaEndTransactionCommandProcessor();

    private final XaPrepareTransactionCommandProcessor xaPrepareTransactionCommandProcessor = new XaPrepareTransactionCommandProcessor();

    private final XaCommitTransactionCommandProcessor xaCommitTransactionCommandProcessor = new XaCommitTransactionCommandProcessor();

    private final XaRollbackTransactionCommandProcessor xaRollbackTransactionCommandProcessor = new XaRollbackTransactionCommandProcessor();

    public SimpleUniverse(UUID id, ValueFactory valueFactory, Collection<Space<?>> spaces) {
        this.id = id;
        this.spaces = Optional.ofNullable(spaces)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .collect(Collectors.toMap(
                        e -> e.getName(),
                        Function.identity())
                );

        this.valueSerializer = valueFactory.serializer();
        this.valueDeserializer = valueFactory.deserializer();
        this.valueTranslator = valueFactory.translator();
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Operations> Space<T> getSpace(String spaceName) {
        return (Space<T>) this.spaces.get(spaceName);
    }

    @Override
    public <R extends Result> R process(Command command) throws XAException {
        final Xid xid = command instanceof XaCommand ? ((XaCommand) command).getXid() : null;
        return this.process(new ContextImpl(xid), command);
    }

    @SuppressWarnings("unchecked")
    public <R extends Result> R process(Context context, Command command) throws XAException {
        return (R) switch (command) {
            case PutCommand cmd -> this.putCommadProcessor.process(context, cmd);
            case RemoveCommand cmd -> this.removeCommadProcessor.process(context, cmd);
            case XaStartTransactionCommand cmd -> this.xaStartTransactionCommandProcessor.process(context, cmd);
            case XaEndTransactionCommand cmd -> this.xaEndTransactionCommandProcessor.process(context, cmd);
            case XaPrepareTransactionCommand cmd -> this.xaPrepareTransactionCommandProcessor.process(context, cmd);
            case XaCommitTransactionCommand cmd -> this.xaCommitTransactionCommandProcessor.process(context, cmd);
            case XaRollbackTransactionCommand cmd -> this.xaRollbackTransactionCommandProcessor.process(context, cmd);
            default -> throw new UnsupportedOperationException("Unsupported command: " + command);
        };
    }

    private class ContextImpl implements Context {
        private final Xid xid;

        private ContextImpl(Xid xid) {
            this.xid = xid;
        }

        @Override
        public ValueSerializer getSerializer() {
            return SimpleUniverse.this.valueSerializer;
        }

        @Override
        public ValueDeserializer getDeserializer() {
            return SimpleUniverse.this.valueDeserializer;
        }

        @Override
        public ValueTranslator getTranslator() {
            return SimpleUniverse.this.valueTranslator;
        }

        @Override
        public Xid getXid() {
            return this.xid;
        }

        @Override
        public Universe getUniverse() {
            return SimpleUniverse.this;
        }
    }
}
