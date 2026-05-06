module io.github.mathter.memifydb.core.data.fasterxml {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.cbor;

    requires transitive io.github.mathter.memifydb.common;

    exports io.github.mathter.memifydb.common.data.fasterxml;

    provides io.github.mathter.memifydb.common.data.ValueFactory with io.github.mathter.memifydb.common.data.fasterxml.FasterXmlValueFactory;
}