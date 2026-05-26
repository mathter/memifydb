package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.IOUtil;
import io.github.mathter.memifydb.command.Result;
import io.github.mathter.memifydb.common.data.ValueDeserializer;
import io.github.mathter.memifydb.common.data.ValueSerializer;
import io.github.mathter.memifydb.common.util.ByteArray;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

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
class ResultSerializerV1 implements io.github.mathter.memifydb.command.ResultSerializer {
    private boolean write(OutputStream os, ValueResult result) throws IOException {
        final byte[] prefix = result.getPrefix();
        final byte[] rawValue = result.getRawValue();

        os.write(prefix);
        os.write(IOUtil.write(result.getSequenceNumber()));
        ByteArray.writeIntRaw(os, rawValue.length);
        os.write(rawValue);

        return true;
    }

    private ByteBuffer write(ValueResult result) throws IOException {
        final byte[] prefix = result.getPrefix();
        final byte[] sequenceNumberBuf = IOUtil.write(result.getSequenceNumber());
        final byte[] rawValue = result.getRawValue();
        final ByteBuffer buf = ByteBuffer.allocate(
                prefix.length
                        + sequenceNumberBuf.length
                        + Integer.BYTES
                        + rawValue.length
        );

        buf.put(prefix);
        buf.put(sequenceNumberBuf);
        buf.putInt(rawValue.length);
        buf.put(rawValue);

        return buf;
    }

    @Override
    public boolean serialize(OutputStream os, Result result) throws IOException {
        return switch (result) {
            case ValueResult rst -> write(os, rst);
            default -> throw new IllegalStateException(
                    String.format("Unknown result type: %s", result)
            );
        };
    }

    @Override
    public ByteBuffer serialize(Result result) throws IOException {
        return (switch (result) {
            case ValueResult rst -> write(rst);
            default -> throw new IllegalStateException(
                    String.format("Unknown result type: %s", result)
            );
        }).rewind();
    }
}
