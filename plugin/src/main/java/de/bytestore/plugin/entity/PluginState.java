package de.bytestore.plugin.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


/**
 * PluginState is an enumeration representing the various states a plugin can be in
 * throughout its lifecycle.
 *
 * Each state corresponds to a specific stage or condition that a plugin might have,
 * and it is associated with a specific string identifier. This enumeration also provides
 * utility methods to retrieve the state identifier or find a PluginState by its identifier.
 *
 * States:
 * - CREATED: The plugin has been created and initialized.
 * - DISABLED: The plugin has been explicitly disabled.
 * - RESOLVED: The plugin dependencies or requirements are resolved.
 * - STARTED: The plugin is currently running.
 * - STOPPED: The plugin has been stopped and is not running.
 * - FAILED: The plugin failed to execute or initialize properly.
 * - UNLOADED: The plugin has been removed or unloaded from the system.
 *
 * Utility Methods:
 * - `getId`: Returns the string identifier of the state.
 * - `fromId`: Retrieves a PluginState based on its string identifier.
 */
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