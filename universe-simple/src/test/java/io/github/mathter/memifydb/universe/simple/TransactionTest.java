package io.github.mathter.memifydb.universe.simple;

import io.github.mathter.memifydb.command.Result;
import io.github.mathter.memifydb.command.SequenceNumber;
import io.github.mathter.memifydb.command.v1.GetCommand;
import io.github.mathter.memifydb.command.v1.PutCommand;
import io.github.mathter.memifydb.command.v1.ValueResult;
import io.github.mathter.memifydb.command.v1.VoidResult;
import io.github.mathter.memifydb.command.v1.XaCommitTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaEndTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaPrepareTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaRecoverTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaRollbackTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaStartTransactionCommand;
import io.github.mathter.memifydb.command.v1.XaWrapperCommand;
import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.xa.Xid;
import io.github.mathter.memifydb.space.KeyValueOperations;
import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.space.SpaceFactory;
import io.github.mathter.memifydb.universe.Universe;
import io.github.mathter.memifydb.universe.UniverseFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import java.util.List;
import java.util.Map;

public class TransactionTest {
    @Test
    public void testCommit() throws XAException {
        SequenceNumber sequenceNumber = new SequenceNumber(1L);
        final Xid xid = Xid.of(0, RandomUtils.nextBytes(10), RandomUtils.nextBytes(10));
        final Space<KeyValueOperations> space = SpaceFactory
                .getInstance(io.github.mathter.memifydb.space.simple.Const.ID)
                .get(RandomStringUtils.random(10));
        final Universe universe = UniverseFactory
                .getInstance(Const.ID)
                .newInstance(
                        Map.of(Const.PROPERTY_SPACES, List.of(space))
                );
        final Value key = universe.getValueFactory().translator().from(RandomStringUtils.randomAlphanumeric(16));
        final Value value = universe.getValueFactory().translator().from(RandomStringUtils.randomAlphanumeric(16));

        final XaStartTransactionCommand startTransactionCommand = new XaStartTransactionCommand(
                sequenceNumber,
                xid,
                XAResource.TMNOFLAGS
        );

        Result result = universe.process(startTransactionCommand);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(VoidResult.class, result.getClass());

        sequenceNumber = sequenceNumber.next();
        final PutCommand putCommand = new PutCommand(
                sequenceNumber,
                space.getName(),
                key,
                value
        );

        sequenceNumber = sequenceNumber.next();
        final XaWrapperCommand<?> wrapperCommand = new XaWrapperCommand<>(
                sequenceNumber,
                xid,
                putCommand
        );
        result = universe.process(wrapperCommand);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(ValueResult.class, result.getClass());

        sequenceNumber = sequenceNumber.next();
        final XaEndTransactionCommand endTransactionCommand = new XaEndTransactionCommand(
                sequenceNumber,
                xid,
                XAResource.TMNOFLAGS
        );
        result = universe.process(endTransactionCommand);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(VoidResult.class, result.getClass());

        sequenceNumber = sequenceNumber.next();
        final XaPrepareTransactionCommand prepareTransactionCommand = new XaPrepareTransactionCommand(
                sequenceNumber,
                xid
        );
        result = universe.process(prepareTransactionCommand);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(VoidResult.class, result.getClass());

        sequenceNumber = sequenceNumber.next();
        final XaRecoverTransactionCommand recoverTransactionCommand = new XaRecoverTransactionCommand(
                sequenceNumber,
                XAResource.TMSTARTRSCAN
        );
        result = universe.process(recoverTransactionCommand);
        Assertions.assertNotNull(result);

        sequenceNumber = sequenceNumber.next();
        final XaCommitTransactionCommand commitTransactionCommand = new XaCommitTransactionCommand(
                sequenceNumber,
                xid,
                true
        );
        result = universe.process(commitTransactionCommand);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(VoidResult.class, result.getClass());

        sequenceNumber = sequenceNumber.next();
        final GetCommand getCommand = new GetCommand(
                sequenceNumber,
                space.getName(),
                key
        );

        result = universe.process(getCommand);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(value, ((ValueResult) result).getValue());
    }

    @Test
    public void testRollback() throws XAException {
        SequenceNumber sequenceNumber = new SequenceNumber(1L);
        final Xid xid = Xid.of(0, RandomUtils.nextBytes(10), RandomUtils.nextBytes(10));
        final Space<KeyValueOperations> space = SpaceFactory
                .getInstance(io.github.mathter.memifydb.space.simple.Const.ID)
                .get(RandomStringUtils.random(10));
        final Universe universe = UniverseFactory
                .getInstance(Const.ID)
                .newInstance(
                        Map.of(Const.PROPERTY_SPACES, List.of(space))
                );
        final Value key = universe.getValueFactory().translator().from(RandomStringUtils.randomAlphanumeric(16));
        final Value value = universe.getValueFactory().translator().from(RandomStringUtils.randomAlphanumeric(16));

        final XaStartTransactionCommand startTransactionCommand = new XaStartTransactionCommand(
                sequenceNumber,
                xid,
                XAResource.TMNOFLAGS
        );

        Result result = universe.process(startTransactionCommand);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(VoidResult.class, result.getClass());

        sequenceNumber = sequenceNumber.next();
        final PutCommand putCommand = new PutCommand(
                sequenceNumber,
                space.getName(),
                key,
                value
        );

        sequenceNumber = sequenceNumber.next();
        final XaWrapperCommand<?> wrapperCommand = new XaWrapperCommand<>(
                sequenceNumber,
                xid,
                putCommand
        );
        result = universe.process(wrapperCommand);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(ValueResult.class, result.getClass());

        sequenceNumber = sequenceNumber.next();
        final XaEndTransactionCommand endTransactionCommand = new XaEndTransactionCommand(
                sequenceNumber,
                xid,
                XAResource.TMNOFLAGS
        );
        result = universe.process(endTransactionCommand);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(VoidResult.class, result.getClass());

        sequenceNumber = sequenceNumber.next();
        final XaPrepareTransactionCommand prepareTransactionCommand = new XaPrepareTransactionCommand(
                sequenceNumber,
                xid
        );
        result = universe.process(prepareTransactionCommand);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(VoidResult.class, result.getClass());

        sequenceNumber = sequenceNumber.next();
        final XaRollbackTransactionCommand rollbackTransactionCommand = new XaRollbackTransactionCommand(
                sequenceNumber,
                xid
        );
        result = universe.process(rollbackTransactionCommand);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(VoidResult.class, result.getClass());

        sequenceNumber = sequenceNumber.next();
        final GetCommand getCommand = new GetCommand(
                sequenceNumber,
                space.getName(),
                key
        );

        result = universe.process(getCommand);
        Assertions.assertNotNull(result);
        Assertions.assertNull(((ValueResult) result).getValue());
    }
}
