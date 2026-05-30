package io.github.mathter.memifydb.universe;

public class UniverseDesc {
    private final String universeName;

    private final String valueFactoryId;

    private final String commandSerilizationFactoryId;

    private final String resultSerilizationFactoryId;

    public UniverseDesc(String universeName,
                        String valueFactoryId,
                        String commandSerilizationFactoryId,
                        String resultSerilizationFactoryId) {
        this.universeName = universeName;
        this.valueFactoryId = valueFactoryId;
        this.commandSerilizationFactoryId = commandSerilizationFactoryId;
        this.resultSerilizationFactoryId = resultSerilizationFactoryId;
    }

    public String getUniverseName() {
        return universeName;
    }

    public String getValueFactoryId() {
        return valueFactoryId;
    }

    public String getCommandSerilizationFactoryId() {
        return commandSerilizationFactoryId;
    }

    public String getResultSerilizationFactoryId() {
        return resultSerilizationFactoryId;
    }
}
