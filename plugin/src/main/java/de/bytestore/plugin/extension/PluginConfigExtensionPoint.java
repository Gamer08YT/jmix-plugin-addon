package de.bytestore.plugin.extension;

import com.vaadin.flow.component.Component;
import org.pf4j.ExtensionPoint;

import java.util.List;

public interface PluginConfigExtensionPoint extends ExtensionPoint {
    /**
     * Saves the current state or configuration of the plugin.
     * This method is intended to persist any changes made to the plugin's settings
     * or data to ensure they are retained for future use.
     */
    void save();

    /**
     * Renders and retrieves a list of components associated with the plugin configuration.
     *
     * @return a list of Component objects representing the rendered elements of the plugin configuration.
     */
    List<Component> render();
}
