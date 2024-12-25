package de.bytestore.plugin.view.plugin;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Plugin;
import de.bytestore.plugin.service.PluginService;
import io.jmix.core.LoadContext;
import io.jmix.core.SaveContext;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Route(value = "plugins/:id", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_Plugin.detail")
@ViewDescriptor(path = "plugin-detail-view.xml")
@EditedEntityContainer("pluginDc")
public class PluginDetailView extends StandardDetailView<Plugin> {

    @Autowired
    private PluginService pluginService;
    @ViewComponent
    private JmixButton downloadButton;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        downloadButton.setVisible(pluginService.isPermitted("downloadPlugin"));
    }


    @Install(to = "pluginDl", target = Target.DATA_LOADER)
    private Plugin customerDlLoadDelegate(final LoadContext<Plugin> loadContext) {
        Object id = loadContext.getId();
        // Here you can load the entity by id from an external storage.
        // Set the loaded entity to the not-new state using EntityStates.setNew(entity, false).
        return pluginService.getPluginCasted(id);
    }

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

    @Subscribe(id = "downloadButton", subject = "clickListener")
    public void onDownloadButtonClick(final ClickEvent<JmixButton> event) {
        pluginService.downloadPlugin(getEditedEntity());
    }


}
