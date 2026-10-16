package de.bytestore.plugin.extension;

import com.vaadin.flow.component.Component;
import org.pf4j.ExtensionPoint;

import java.util.List;

/**
 * Provides the contents of a plugin's About tab. Implementations may return Jmix Flow UI components,
 * which are Vaadin components and can be composed in the same way as in a regular Jmix view.
 */
public interface PluginAboutExtensionPoint extends ExtensionPoint {
    /**
     * Creates the UI shown on the plugin's About tab.
     *
     * @return components to add to the About tab, or an empty list
     */
    List<Component> render();
}
