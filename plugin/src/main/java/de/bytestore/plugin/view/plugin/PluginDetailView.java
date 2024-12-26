package de.bytestore.plugin.view.plugin;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Plugin;
import de.bytestore.plugin.service.PluginService;
import io.jmix.core.LoadContext;
import io.jmix.core.SaveContext;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.action.DialogAction;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * PluginDetailView is responsible for providing the detailed view of a Plugin entity.
 * This view allows users to interact with a plugin entity, including viewing its details,
 * downloading the plugin, and deleting it with confirmation.
 *
 * It extends the functionality of `StandardDetailView`.
 *
 * Annotations:
 * - @Route: Defines the URL routing path for this view.
 * - @ViewController: Specifies the view controller's unique ID.
 * - @ViewDescriptor: Defines the descriptor file for the view.
 * - @EditedEntityContainer: Specifies the container ID managing the lifecycle of the edited Plugin entity.
 *
 * Components and Dependencies:
 * - PluginService: Service responsible for business logic related to plugins.
 * - Dialogs: Service for displaying dialog windows.
 * - MessageBundle: Component for localized messages.
 * - JmixButton: UI component for triggering actions.
 *
 * Event Handlers:
 * - onBeforeShow: An event triggered before the view is displayed. It configures the visibility
 *   of the download button based on user permissions.
 * - onDownloadButtonClick: Handles the action when the download button is clicked and initiates
 *   the download process for the current plugin.
 * - onRemoveButtonClick: Displays a confirmation dialog to the user before deleting the current plugin
 *   and performs the deletion if confirmed.
 *
 * Delegated Installations:
 * - customerDlLoadDelegate: Responsible for customizing the data loading logic for the edited plugin entity.
 *   Allows integration with external storage or data sources to fetch the entity by ID.
 * - saveDelegate: Provides custom save logic for the edited plugin entity. It handles saving to an external
 *   storage system and ensures the new or updated state is consistent with the framework.
 */
@Route(value = "plugins/:id", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_Plugin.detail")
@ViewDescriptor(path = "plugin-detail-view.xml")
@EditedEntityContainer("pluginDc")
public class PluginDetailView extends StandardDetailView<Plugin> {

    @Autowired
    private PluginService pluginService;

    @ViewComponent
    private JmixButton downloadButton;

    @Autowired
    private Dialogs dialogs;

    @ViewComponent
    private MessageBundle messageBundle;

    @ViewComponent
    private JmixButton removeButton;
    @Autowired
    private Notifications notifications;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        downloadButton.setVisible(pluginService.isPermitted("downloadPlugin"));
        removeButton.setVisible(pluginService.isPermitted("deletePlugin"));
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

    /**
     * Handles the click event when the download button is clicked.
     * This method triggers the download process for the currently edited plugin
     * using the PluginService.
     *
     * @param event the click event triggered by the download button.
     */
    @Subscribe(id = "downloadButton", subject = "clickListener")
    public void onDownloadButtonClick(final ClickEvent<JmixButton> event) {
        pluginService.downloadPlugin(getEditedEntity());
    }

    /**
     * Handles the click event of the remove button.
     * This method displays a confirmation dialog to the user before attempting to delete
     * the currently edited plugin. If the user confirms the deletion, the plugin is deleted
     * using the PluginService and the result of the operation (success or failure) is
     * displayed as a notification.
     *
     * @param event the click event triggered by the remove button.
     */
    @Subscribe(id = "removeButton", subject = "clickListener")
    public void onRemoveButtonClick(final ClickEvent<JmixButton> event) {
        dialogs.createOptionDialog().withHeader(messageBundle.getMessage("delete")).withText(messageBundle.formatMessage("deleteWarning", getEditedEntity().getId())).withActions(new DialogAction(DialogAction.Type.YES).withHandler(actionPerformedEvent -> {

            if (pluginService.delete(getEditedEntity()))
                notifications.create(messageBundle.formatMessage("pluginDeleted", getEditedEntity().getId())).withType(Notifications.Type.SUCCESS).withPosition(Notification.Position.BOTTOM_END).show();
            else
                notifications.create(messageBundle.formatMessage("pluginDeleteFailed", getEditedEntity().getId())).withType(Notifications.Type.ERROR).withPosition(Notification.Position.BOTTOM_END).show();

        }), new DialogAction(DialogAction.Type.CANCEL)).open();
    }


}
