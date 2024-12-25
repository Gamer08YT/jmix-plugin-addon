package de.bytestore.plugin.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum PluginState implements EnumClass<String> {
    CREATED("CREATED"),
    DISABLED("DISABLED"),
    RESOLVED("RESOLVED"),
    STARTED("STARTED"),
    STOPPED("STOPPED"),
    FAILED("FAILED"),
    UNLOADED("UNLOADED");

    private final String id;

    PluginState(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static PluginState fromId(String id) {
        for (PluginState at : PluginState.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}