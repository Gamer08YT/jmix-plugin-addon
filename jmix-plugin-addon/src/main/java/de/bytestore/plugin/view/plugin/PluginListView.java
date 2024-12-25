package de.bytestore.plugin.view.plugin;

import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Plugin;
import de.bytestore.plugin.service.PluginService;
import io.jmix.core.LoadContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "plugins", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_Plugin.list")
@ViewDescriptor(path = "plugin-list-view.xml")
@LookupComponent("pluginsDataGrid")
@DialogMode(width = "50em")
public class PluginListView extends StandardListView<Plugin> {

    @Autowired
    private PluginService pluginService;

    @Install(to = "pluginsDl", target = Target.DATA_LOADER)
    protected List<Plugin> pluginsDlLoadDelegate(LoadContext<Plugin> loadContext) {
        // Here you can load entities from an external storage.
        // Set the loaded entities to the not-new state using EntityStates.setNew(entity, false).
        return pluginService.castPlugins();
    }
}
