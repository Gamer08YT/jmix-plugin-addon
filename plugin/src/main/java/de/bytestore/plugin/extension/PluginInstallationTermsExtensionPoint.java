package de.bytestore.plugin.extension;

import com.vaadin.flow.component.Component;
import org.pf4j.ExtensionPoint;

import java.util.List;

/**
 * Lets a plugin customize the installation terms dialog with Jmix Flow UI components.
 * The add-on always adds its own required acceptance checkbox and cancel/continue actions.
 */
public interface PluginInstallationTermsExtensionPoint extends ExtensionPoint {
    /** @return the dialog title; defaults to "Terms and conditions" when blank or null */
    default String title() {
        return "Terms and conditions";
    }

    /** @return Jmix Flow UI components describing the terms */
    List<Component> render();
}
