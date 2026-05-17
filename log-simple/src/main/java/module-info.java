module log.simple {
    requires io.github.mathter.memifydb.common;
    requires io.github.mathter.memifydb.log.api;
    requires org.apache.commons.lang3;
    requires java.logging;

    provides io.github.mathter.memifydb.log.LogFactory with io.github.mathter.memifydb.log.simple.FileLogFactory;
}