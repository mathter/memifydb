module io.github.mathter.memifydb.core.data.fasterxml {
    requires tools.jackson.dataformat.cbor;
    requires tools.jackson.core;

    requires transitive io.github.mathter.memifydb.common;

    exports io.github.mathter.memifydb.common.data.fasterxml;

    provides io.github.mathter.memifydb.common.data.ValueFactory with io.github.mathter.memifydb.common.data.fasterxml.FasterXmlValueFactory;
}