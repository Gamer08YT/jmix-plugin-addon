package de.bytestore.welcome.extensions;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;
import de.bytestore.plugin.extension.PluginConfigExtensionPoint;
import de.bytestore.plugin.service.PluginService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Component
public class ConfigExtension implements PluginConfigExtensionPoint {
    @Autowired
    private PluginService pluginService;

    // Create Dummy Field.
    private final TextField username = new TextField("Username");

    // Create Dummy Checkbox.
    private final Checkbox checkbox = new Checkbox("Enable Debug Mode.");


    /**
     * Saves the current state or configuration of the plugin.
     * This method is intended to persist any changes made to the plugin's settings
     * or data to ensure they are retained for future use.
     */
    @Override
    public void save() {
        pluginService.setValue("welcome.username", username.getValue());
        pluginService.setValue("welcome.debug", checkbox.getValue());
    }

    /**
     * Renders and retrieves a list of components associated with the plugin configuration.
     *
     * @return a list of Component objects representing the rendered elements of the plugin configuration.
     */
    @Override
    public List<com.vaadin.flow.component.Component> render() {
        List<Component> componentsIO = new java.util.ArrayList<>();

        username.setValue(pluginService.getValue("welcome.username", "JmixUser"));
        checkbox.setValue((Boolean) pluginService.getValue("welcome.debug", false));

        componentsIO.add(username);
        componentsIO.add(checkbox);

        return componentsIO;
    }
}
