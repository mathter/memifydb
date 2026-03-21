module io.github.mathter.memifydb.core.data.fasterxml {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.cbor;

    requires transitive io.github.mathter.memifydb.core;

    exports io.github.mathter.memifydb.core.data.fasterxml;

    provides io.github.mathter.memifydb.core.data.ValueFactory with io.github.mathter.memifydb.core.data.fasterxml.FasterXmlValueFactory;
}