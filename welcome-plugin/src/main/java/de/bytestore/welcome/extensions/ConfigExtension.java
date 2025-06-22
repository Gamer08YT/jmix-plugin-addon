package de.bytestore.welcome.extensions;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;
import de.bytestore.plugin.AutowireLoader;
import de.bytestore.plugin.extension.PluginConfigExtensionPoint;
import de.bytestore.plugin.service.ConfigService;
import de.bytestore.plugin.service.PluginService;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Extension
public class ConfigExtension implements PluginConfigExtensionPoint {

    // Current Workaround for using external Beans...
    // https://github.com/Gamer08YT/hostinger2024/blob/4062136bc1a8aa33bc80ba7ab9ced46892cb04b2/src/main/java/de/bytestore/hostinger/AutowireLoader.java#L19
    // https://byte-storede.github.io/Hostinger-Docs/autowired.html
    private final PluginService pluginService = AutowireLoader.getBean(PluginService.class);

    // Create Dummy Field.
    private final TextField username = new TextField("Username");

    // Create Dummy Checkbox.
    private final Checkbox checkbox = new Checkbox("Enable Debug Mode.");
    private final ConfigService configService;

    public ConfigExtension(ConfigService configService) {
        this.configService = configService;
    }


    /**
     * Saves the current state or configuration of the plugin.
     * This method is intended to persist any changes made to the plugin's settings
     * or data to ensure they are retained for future use.
     */
    @Override
    public void save() {
        configService.setValue("welcome.username", username.getValue());
        configService.setValue("welcome.debug", checkbox.getValue());
    }

    /**
     * Renders and retrieves a list of components associated with the plugin configuration.
     *
     * @return a list of Component objects representing the rendered elements of the plugin configuration.
     */
    @Override
    public List<com.vaadin.flow.component.Component> render() {
        List<com.vaadin.flow.component.Component> componentsIO = new ArrayList<>();

        username.setValue((String) configService.getValue("welcome.username", "JmixUser"));
        checkbox.setValue((Boolean) configService.getValue("welcome.debug", false));

        componentsIO.add(username);
        componentsIO.add(checkbox);

        return componentsIO;
    }
}
