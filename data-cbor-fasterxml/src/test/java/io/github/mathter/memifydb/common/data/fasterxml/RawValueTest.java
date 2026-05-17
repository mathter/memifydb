package io.github.mathter.memifydb.common.data.fasterxml;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.cbor.CBORMapper;

public class RawValueTest {
    @Test
    public void test() throws Exception {
        final ObjectMapper mapper = new CBORMapper();
        final String value = RandomStringUtils.random(10);
        final byte[] raw = mapper.writeValueAsBytes(value);
        final RawValue rawValue = new RawValue(mapper, raw);

        Assertions.assertEquals(raw, rawValue.getRaw());

        final String cached = rawValue.get();
        Assertions.assertEquals(value, cached);

        final String cached2 = rawValue.get();
        Assertions.assertEquals(value, cached2);
    }
}
