package de.bytestore.plugin.entity;

import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

/**
 * Represents a plugin entity that contains metadata about a plugin in the system.
 *
 * The state of the plugin is represented as a String but corresponds to an enum value
 * from the {@link PluginState} enum, which provides possible states a plugin can have.
 */
@JmixEntity(name = "plugin_Plugin")
public class Plugin {
    @JmixId
    @InstanceName
    private String id;

    private String path;

    private String description;

    private String version;

    private String license;

    private String provider;

    private String requires;

    private String state;

    public PluginState getState() {
        return state == null ? null : PluginState.fromId(state);
    }

    public void setState(PluginState state) {
        this.state = state == null ? null : state.getId();
    }

    public String getRequires() {
        return requires;
    }

    public void setRequires(String requires) {
        this.requires = requires;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}