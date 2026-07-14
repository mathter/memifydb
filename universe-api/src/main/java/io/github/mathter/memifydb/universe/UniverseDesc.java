package io.github.mathter.memifydb.universe;

import java.util.Objects;

public class UniverseDesc {
    private String universeName;

    private String valueFactoryId;

    public UniverseDesc() {
    }

    public UniverseDesc(String universeName, String valueFactoryId) {
        this.universeName = universeName;
        this.valueFactoryId = valueFactoryId;
    }

    public void setUniverseName(String universeName) {
        this.universeName = universeName;
    }

    public String getUniverseName() {
        return universeName;
    }

    public void setValueFactoryId(String valueFactoryId) {
        this.valueFactoryId = valueFactoryId;
    }

    public String getValueFactoryId() {
        return valueFactoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UniverseDesc that = (UniverseDesc) o;
        return Objects.equals(universeName, that.universeName) && Objects.equals(valueFactoryId, that.valueFactoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(universeName, valueFactoryId);
    }
}
