package de.bytestore.plugin.view.plugin;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Plugin;
import de.bytestore.plugin.extension.PluginConfigExtensionPoint;
import de.bytestore.plugin.service.PluginService;
import io.jmix.core.EntityStates;
import io.jmix.core.LoadContext;
import io.jmix.core.SaveContext;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.action.DialogAction;
import io.jmix.flowui.backgroundtask.BackgroundTask;
import io.jmix.flowui.backgroundtask.BackgroundWorker;
import io.jmix.flowui.backgroundtask.TaskLifeCycle;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.reflections.Reflections.log;

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

    @Autowired
    private ViewNavigators viewNavigators;

    @Autowired
    private BackgroundWorker backgroundWorker;

    @Autowired
    private EntityStates entityStates;

    @ViewComponent
    private VerticalLayout settingsLayout;

    @ViewComponent
    private Paragraph noSettingsNote;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        downloadButton.setVisible(pluginService.isPermitted("downloadPlugin"));
        removeButton.setVisible(pluginService.isPermitted("deletePlugin"));

        List<Component> settingsUI = getSettingsUI();

        log.info("A: {}", pluginService.getManager().getExtensions(PluginConfigExtensionPoint.class));
        log.info("B: {}", pluginService.getManager().getExtensions(PluginConfigExtensionPoint.class), getEditedEntity().getId());

        if (!settingsUI.isEmpty()) {
            settingsLayout.add(settingsUI);

            noSettingsNote.setVisible(false);
        }
    }

    /**
     * Loads a plugin entity based on the given load context.
     *
     * This method uses the provided load context's identifier to retrieve a plugin
     * from an external storage and ensures that the loaded entity's state is correctly set to not-new.
     *
     * @param loadContext the context containing the identifier of the plugin to be loaded
     * @return the loaded Plugin entity, or null if the identifier is invalid or no plugin is found
     */
    @Install(to = "pluginDl", target = Target.DATA_LOADER)
    private Plugin pluginDlLoadDelegate(final LoadContext<Plugin> loadContext) {
        Object id = loadContext.getId();

        Plugin objectIO = pluginService.getPluginCasted(id);

        // Set the loaded entity to the not-new state.
        entityStates.setNew(objectIO, false);

        return objectIO;
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
     * Persists the given save context by saving the currently edited plugin entity into an external storage.
     * This method ensures that the plugin entity is properly updated and returns it
     * as part of the result set in a not-new state.
     *
     * @param saveContext the context containing details of the entities to be saved
     * @return a set of saved entities, which includes the plugin entity
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

            // Handle Deletion Process via Background Task.
            backgroundWorker.handle(new BackgroundTask<Boolean, Boolean>(TimeUnit.MINUTES.toSeconds(1)) {
                @Override
                public Boolean run(TaskLifeCycle<Boolean> taskLifeCycle) throws Exception {
                    return pluginService.delete(getEditedEntity());
                }

                /**
                 * Called by the execution environment in UI thread when the task is completed.
                 *
                 * @param result result of execution returned by {@link #run(TaskLifeCycle)} method
                 */
                @Override
                public void done(Boolean result) {
                    if (result) {
                        notifications.create(messageBundle.formatMessage("pluginDeleted", getEditedEntity().getId())).withType(Notifications.Type.SUCCESS).withPosition(Notification.Position.BOTTOM_END).show();

                        if (this.getOwnerView() != null)
                            // Navigate to Plugin Overview.
                            viewNavigators.view(this.getOwnerView(), PluginListView.class).navigate();
                    } else
                        notifications.create(messageBundle.formatMessage("pluginDeleteFailed", getEditedEntity().getId())).withType(Notifications.Type.ERROR).withPosition(Notification.Position.BOTTOM_END).show();

                }
            }).execute();
        }), new DialogAction(DialogAction.Type.CANCEL)).open();
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
        if (getPluginConfig() != null)
            return getPluginConfig().render();
        return new ArrayList<>();
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
