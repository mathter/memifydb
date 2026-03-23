package io.github.mathter.memifydb.log.simple;

import io.github.mathter.memifydb.core.command.CommandSerializationFactory;
import io.github.mathter.memifydb.log.Log;
import io.github.mathter.memifydb.log.LogFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class FileLogFactory extends LogFactory {
    public static final int DEFAULT_FILE_MAX_SIZE = 100_000_000;

    public static final String DEFAILT_FILE_NAME_PREFIX = "fl-";

    public static final String DEFAULT_FILE_NAME_POSTFIX = ".wal";

    public static final String ID = "simple";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public Log get(Map<?, ?> properties) {
        return new FileLog(
                this.buildSerializationFactory(properties),
                this.buildBase(properties),
                this.buildFileNamePrefix(properties),
                this.buildFileNamePostfix(properties),
                this.buildFileMaxSize(properties)
        );
    }

    private static CommandSerializationFactory buildSerializationFactory(Map<?, ?> properties) {
        final CommandSerializationFactory result;
        final Object property = properties.get(Const.COMMAND_SERELIZATION_FACTORY);

        if (property != null) {
            if (property instanceof CommandSerializationFactory factory) {
                result = factory;
            } else if (property instanceof String id) {
                result = CommandSerializationFactory.get(id);
            } else {
                throw new IllegalStateException(
                        String.format("Illegal property '%s'", property)
                );
            }
        } else {
            throw new IllegalStateException(
                    String.format("There is no parameter '%s'!", Const.COMMAND_SERELIZATION_FACTORY)
            );
        }

        return result;
    }

    private static Path buildBase(Map<?, ?> properties) {
        final Path result;
        final Object property = properties.get(Const.LOG_ROOT_DIR);

        if (property != null) {
            if (property instanceof String string) {
                result = Paths.get(string);
            } else if (property instanceof Path path) {
                result = path;
            } else if (property instanceof File file) {
                result = file.toPath();
            } else {
                throw new IllegalStateException(
                        String.format("Illegal property '%s'", property)
                );
            }
        } else {
            throw new IllegalStateException(
                    String.format("There is no parameter '%s'!", Const.LOG_ROOT_DIR)
            );
        }

        if (!Files.isDirectory(result)) {
            throw new IllegalStateException(result + " is not a directory!");
        }

        return result;
    }

    private static String buildFileNamePrefix(Map<?, ?> properties) {
        final String result;
        final Object property = properties.get(Const.FILE_NAME_PREFIX);

        if (property != null) {
            if (property instanceof String string) {
                result = string;
            } else {
                throw new IllegalStateException(
                        String.format("Illegal property '%s'", property)
                );
            }
        } else {
            result = DEFAILT_FILE_NAME_PREFIX;
        }

        return result;
    }

    private static String buildFileNamePostfix(Map<?, ?> properties) {
        final String result;
        final Object property = properties.get(Const.FILE_NAME_POSTFIX);

        if (property != null) {
            if (property instanceof String string) {
                result = string;
            } else {
                throw new IllegalStateException(
                        String.format("Illegal property '%s'", property)
                );
            }
        } else {
            result = DEFAULT_FILE_NAME_POSTFIX;
        }

        return result;
    }

    private static int buildFileMaxSize(Map<?, ?> properties) {
        final int result;
        final Object property = properties.get(Const.FILE_MAX_SIZE);

        if (property != null) {
            if (property instanceof String string) {
                result = Integer.parseInt(string);
            } else if (property instanceof Number number) {
                result = number.intValue();
            } else {
                throw new IllegalStateException(
                        String.format("Illegal property '%s'", property)
                );
            }
        } else {
            result = DEFAULT_FILE_MAX_SIZE;
        }

        return result;
    }
}
