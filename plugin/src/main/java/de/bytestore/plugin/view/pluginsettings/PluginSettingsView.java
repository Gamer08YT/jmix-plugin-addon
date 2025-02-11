package de.bytestore.plugin.view.pluginsettings;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Plugin;
import de.bytestore.plugin.extension.PluginConfigExtensionPoint;
import de.bytestore.plugin.service.PluginService;
import io.jmix.flowui.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * PluginSettingsView is a detail view class for managing the settings of a specific plugin.
 * It extends the StandardDetailView, providing a view for interacting with plugin-related
 * data and its configuration settings.
 *
 * The view is associated with the Plugin entity and is dynamically loaded
 * using the specified route, layout, and view descriptor.
 */
@Route(value = "plugin/settings/:id", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_PluginSettingsView")
@ViewDescriptor(path = "plugin-settings-view.xml")
public class PluginSettingsView extends StandardDetailView<Plugin> {
    private static final Logger log = LoggerFactory.getLogger(PluginSettingsView.class);
    @Autowired
    private PluginService pluginService;

    /**
     * Initializes the plugin settings view when the InitEvent is fired.
     *
     * This method is triggered during the initialization of the view and adds
     * the settings UI components to the view's content.
     *
     * @param event the initialization event that triggers this method
     */
    @Subscribe
    public void onInit(final InitEvent event) {
        getContent().add(getSettingsUI());
    }

    /**
     * Triggered before saving the entity in the view. This method logs the plugin
     * being saved and invokes the save operation for the corresponding plugin configuration.
     *
     * @param event the event containing details about the save operation being executed
     */
    @Subscribe
    public void onBeforeSave(final BeforeSaveEvent event) {
        log.info("Saving Settings for Plugin: {}.", getEditedEntity().getId());

        this.getPluginConfig().save();
    }

    /**
     * Retrieves the settings UI components for the plugin.
     *
     * This method invokes the rendering process of the plugin configuration,
     * generating a list of UI components that represent the settings interface
     * for the current plugin.
     *
     * @return a list of Component objects that represent the plugin's settings UI.
     */
    private List<Component> getSettingsUI() {
        return getPluginConfig().render();
    }

    /**
     * Retrieves the plugin configuration extension instance for the currently edited entity.
     *
     * This method fetches an instance of the {@link PluginConfigExtensionPoint} associated with
     * the plugin identified by the ID of the currently edited entity and retrieves the first
     * extension available for the given plugin.
     *
     * @return an instance of {@link PluginConfigExtensionPoint*/
    private PluginConfigExtensionPoint getPluginConfig() {
        return ((PluginConfigExtensionPoint) pluginService.getExtension(PluginConfigExtensionPoint.class, getEditedEntity().getId()).get(0));
    }


}