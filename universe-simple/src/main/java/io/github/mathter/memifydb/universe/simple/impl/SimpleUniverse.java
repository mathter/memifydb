package io.github.mathter.memifydb.universe.simple.impl;

import io.github.mathter.memifydb.command.Command;
import io.github.mathter.memifydb.command.Result;
import io.github.mathter.memifydb.command.v1.ExceptionResult;
import io.github.mathter.memifydb.command.v1.GetCommand;
import io.github.mathter.memifydb.command.v1.PutCommand;
import io.github.mathter.memifydb.command.v1.RemoveCommand;
import io.github.mathter.memifydb.command.v1.XaCommitTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaEndTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaPrepareTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaRecoverTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaRollbackTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaStartTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaWrapperCommand;
import io.github.mathter.memifydb.command.xa.XaCommand;
import io.github.mathter.memifydb.common.data.ValueDeserializer;
import io.github.mathter.memifydb.common.data.ValueFactory;
import io.github.mathter.memifydb.common.data.ValueSerializer;
import io.github.mathter.memifydb.common.data.ValueTranslator;
import io.github.mathter.memifydb.space.Operations;
import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.universe.Context;
import io.github.mathter.memifydb.universe.Universe;
import io.github.mathter.memifydb.universe.simple.impl.v1.GetCommadProcessor;
import io.github.mathter.memifydb.universe.simple.impl.v1.PutCommadProcessor;
import io.github.mathter.memifydb.universe.simple.impl.v1.RemoveCommadProcessor;
import io.github.mathter.memifydb.universe.simple.impl.v1.XaCommitTransactionCommandProcessor;
import io.github.mathter.memifydb.universe.simple.impl.v1.XaEndTransactionCommandProcessor;
import io.github.mathter.memifydb.universe.simple.impl.v1.XaPrepareTransactionCommandProcessor;
import io.github.mathter.memifydb.universe.simple.impl.v1.XaRecoverTransactionCommandProcessor;
import io.github.mathter.memifydb.universe.simple.impl.v1.XaRollbackTransactionCommandProcessor;
import io.github.mathter.memifydb.universe.simple.impl.v1.XaStartTransactionCommandProcessor;
import io.github.mathter.memifydb.universe.simple.impl.v1.XaWrapperCommandProcessor;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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

    private final String name;

    private final Map<String, Space<?>> spaces;

    private final XAResource xaResource = new XaResource(this);

    private final ValueFactory valueFactory;

    private final ValueSerializer valueSerializer;

    private final ValueDeserializer valueDeserializer;

    private final ValueTranslator valueTranslator;

    private final PutCommadProcessor putCommadProcessor = new PutCommadProcessor();

    private final RemoveCommadProcessor removeCommadProcessor = new RemoveCommadProcessor();

    private final GetCommadProcessor getCommadProcessor = new GetCommadProcessor();

    private final XaStartTransactionCommandProcessor xaStartTransactionCommandProcessor = new XaStartTransactionCommandProcessor();

    private final XaEndTransactionCommandProcessor xaEndTransactionCommandProcessor = new XaEndTransactionCommandProcessor();

    private final XaPrepareTransactionCommandProcessor xaPrepareTransactionCommandProcessor = new XaPrepareTransactionCommandProcessor();

    private final XaCommitTransactionCommandProcessor xaCommitTransactionCommandProcessor = new XaCommitTransactionCommandProcessor();

    private final XaRollbackTransactionCommandProcessor xaRollbackTransactionCommandProcessor = new XaRollbackTransactionCommandProcessor();

    private final XaWrapperCommandProcessor xaWrapperCommandProcessor = new XaWrapperCommandProcessor();

    private final XaRecoverTransactionCommandProcessor xaRecoverTransactionCommandProcessor = new XaRecoverTransactionCommandProcessor();

    public SimpleUniverse(UUID id, String name, ValueFactory valueFactory, Collection<Space<?>> spaces) {
        this.id = id;
        this.name = name;
        this.spaces = Optional.ofNullable(spaces)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .collect(Collectors.toMap(
                        e -> e.getName(),
                        Function.identity())
                );

        this.valueFactory = valueFactory;
        this.valueSerializer = valueFactory.serializer();
        this.valueDeserializer = valueFactory.deserializer();
        this.valueTranslator = valueFactory.translator();
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Collection<Space<?>> getSpaces() {
        return List.copyOf(this.spaces.values());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Operations> Space<T> getSpace(String spaceName) {
        final Space<T> space = (Space<T>) this.spaces.get(spaceName);

        if (space != null) {
            return space;
        } else {
            throw new NoSuchElementException("Space not found: " + spaceName);
        }
    }

    @Override
    public ValueFactory getValueFactory() {
        return this.valueFactory;
    }

    @Override
    public XAResource getXAResource() {
        return this.xaResource;
    }

    @Override
    public Result process(Command command) {
        Result result;
        try {
            final Xid xid = command instanceof XaCommand ? ((XaCommand) command).getXid() : null;
            result = this.process(new ContextImpl(xid), command);
        } catch (Throwable t) {
            result = new ExceptionResult(command.getSequenceNumber(), t.toString());
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result process(Context context, Command command) {
        return switch (command) {
            case PutCommand cmd -> this.putCommadProcessor.process(context, cmd);
            case RemoveCommand cmd -> this.removeCommadProcessor.process(context, cmd);
            case GetCommand cmd -> this.getCommadProcessor.process(context, cmd);
            case XaStartTransactionCommand cmd -> this.xaStartTransactionCommandProcessor.process(context, cmd);
            case XaEndTransactionCommand cmd -> this.xaEndTransactionCommandProcessor.process(context, cmd);
            case XaPrepareTransactionCommand cmd -> this.xaPrepareTransactionCommandProcessor.process(context, cmd);
            case XaCommitTransactionCommand cmd -> this.xaCommitTransactionCommandProcessor.process(context, cmd);
            case XaRollbackTransactionCommand cmd -> this.xaRollbackTransactionCommandProcessor.process(context, cmd);
            case XaWrapperCommand<?> cmd -> this.xaWrapperCommandProcessor.process(context, cmd);
            case XaRecoverTransactionCommand cmd -> this.xaRecoverTransactionCommandProcessor.process(context, cmd);
            default -> throw new UnsupportedOperationException("Unsupported command: " + command);
        };
    }

    private class ContextImpl implements Context {
        private Xid xid;

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
        public void setXid(Xid xid) {
            this.xid = xid;
        }

        @Override
        public Universe getUniverse() {
            return SimpleUniverse.this;
        }
    }
}
