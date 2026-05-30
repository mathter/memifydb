package io.github.mathter.memifydb.universe.simple.impl.v1;

import io.github.mathter.memifydb.command.Result;
import io.github.mathter.memifydb.command.v1.ValueResult;
import io.github.mathter.memifydb.command.v1.XaRecoverTransactionCommand;
import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.universe.Context;
import io.github.mathter.memifydb.universe.simple.impl.CommandProcessor;

import javax.transaction.xa.Xid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class XaRecoverTransactionCommandProcessor implements CommandProcessor<XaRecoverTransactionCommand> {
    @Override
    public Result process(Context context, XaRecoverTransactionCommand command) {
        final Collection<Xid> xids = new ArrayList<>();

        try {
            for (Space<?> space : context.getUniverse().getSpaces()) {
                final Xid[] tmp = space.xaResource().recover(command.getFlags());

                if (tmp != null) {
                    xids.addAll(Arrays.asList(tmp));
                }
            }
        } catch (Throwable t) {

        }

        return new ValueResult(
                command.getSequenceNumber(),
                context.getTranslator().from(xids.toArray(Xid[]::new))
        );
    }
}
