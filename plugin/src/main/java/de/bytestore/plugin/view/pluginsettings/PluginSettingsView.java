package de.bytestore.plugin.view.pluginsettings;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Plugin;
import de.bytestore.plugin.extension.PluginConfigExtensionPoint;
import de.bytestore.plugin.service.PluginService;
import io.jmix.core.LoadContext;
import io.jmix.core.SaveContext;
import io.jmix.flowui.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * PluginSettingsView is a detail view class for managing the settings of a specific plugin.
 * It extends the StandardDetailView, providing a view for interacting with plugin-related
 * data and its configuration settings.
 *
 * The view is associated with the Plugin entity and is dynamically loaded
 * using the specified route, layout, and view descriptor.
 */
@Route(value = "plugins/settings/:id", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_PluginSettingsView")
@ViewDescriptor(path = "plugin-settings-view.xml")
@EditedEntityContainer("pluginDc")
public class PluginSettingsView extends StandardDetailView<Plugin> {
    private static final Logger log = LoggerFactory.getLogger(PluginSettingsView.class);

    @Autowired
    private PluginService pluginService;


    /**
     * A delegate method used to load a {@link Plugin} entity in the data loader.
     *
     * This method retrieves a plugin entity by its identifier from an external storage,
     * using the provided {@link LoadContext}. The loaded entity is expected to be set
     * to the not-new state via the appropriate utility (e.g., {@code EntityStates.setNew(entity, false)}).
     *
     * @param loadContext the context providing information for loading the entity, including its identifier
     * @return the loaded {@link Plugin} entity casted to the appropriate type, or null if no entity is found
     */
    @Install(to = "pluginDl", target = Target.DATA_LOADER)
    private Plugin customerDlLoadDelegate(final LoadContext<Plugin> loadContext) {
        Object id = loadContext.getId();
        // Here you can load the entity by id from an external storage.
        // Set the loaded entity to the not-new state using EntityStates.setNew(entity, false).
        return pluginService.getPluginCasted(id);
    }

    /**
     * A delegate method used to save a {@link Plugin} entity to an external storage.
     *
     * This method is installed as the data context save delegate. It allows customization
     * of the saving process, enabling the entity to be saved to an external storage.
     * The saved entity is returned in a set, and its state can be updated as not-new or
     * with a newly assigned ID if applicable.
     *
     * @param saveContext the context providing information about the entities to save,
     *                    including modified, removed, and added entities.
     * @return a set containing the saved entities after the save operation is completed.
     */
    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> saveDelegate(final SaveContext saveContext) {
        Plugin entity = getEditedEntity();
        // Here you can save the entity to an external storage and return the saved instance.
        // Set the returned entity to the not-new state using EntityStates.setNew(entity, false).
        // If the new entity ID is assigned by the storage, set the ID to the original instance too
        // to let the framework match the saved instance with the original one.
        Plugin saved = entity;
        return Set.of(saved);
    }

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