package de.bytestore.plugin.view.plugin;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bytestore.plugin.entity.Plugin;
import de.bytestore.plugin.extension.PluginAboutExtensionPoint;
import de.bytestore.plugin.extension.PluginConfigExtensionPoint;
import de.bytestore.plugin.service.PluginService;
import de.bytestore.plugin.service.InstallationTermsContent;
import io.jmix.core.DataManager;
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
import io.jmix.flowui.component.tabsheet.JmixTabSheet;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;


/**
 * PluginDetailView is responsible for providing the detailed view of a Plugin entity.
 * This view allows users to interact with a plugin entity, including viewing its details,
 * downloading the plugin, and deleting it with confirmation.
 * <p>
 * It extends the functionality of `StandardDetailView`.
 * <p>
 * Annotations:
 * - @Route: Defines the URL routing path for this view.
 * - @ViewController: Specifies the view controller's unique ID.
 * - @ViewDescriptor: Defines the descriptor file for the view.
 * - @EditedEntityContainer: Specifies the container ID managing the lifecycle of the edited Plugin entity.
 * <p>
 * Components and Dependencies:
 * - PluginService: Service responsible for business logic related to plugins.
 * - Dialogs: Service for displaying dialog windows.
 * - MessageBundle: Component for localized messages.
 * - JmixButton: UI component for triggering actions.
 * <p>
 * Event Handlers:
 * - onBeforeShow: An event triggered before the view is displayed. It configures the visibility
 * of the download button based on user permissions.
 * - onDownloadButtonClick: Handles the action when the download button is clicked and initiates
 * the download process for the current plugin.
 * - onRemoveButtonClick: Displays a confirmation dialog to the user before deleting the current plugin
 * and performs the deletion if confirmed.
 * <p>
 * Delegated Installations:
 * - customerDlLoadDelegate: Responsible for customizing the data loading logic for the edited plugin entity.
 * Allows integration with external storage or data sources to fetch the entity by ID.
 * - saveDelegate: Provides custom save logic for the edited plugin entity. It handles saving to an external
 * storage system and ensures the new or updated state is consistent with the framework.
 */
@Route(value = "plugins/:id", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_Plugin.detail")
@ViewDescriptor(path = "plugin-detail-view.xml")
@EditedEntityContainer("pluginDc")
public class PluginDetailView extends StandardDetailView<Plugin> {
    private static final Logger log = LoggerFactory.getLogger(PluginDetailView.class);
    private static final int MAX_ABOUT_HTML_BYTES = 1024 * 1024;


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

    @ViewComponent
    private JmixButton termsButton;

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
    private VerticalLayout changelogsLayout;

    @ViewComponent
    private VerticalLayout aboutLayout;

    @ViewComponent
    private Paragraph noSettingsNote;

    @ViewComponent
    private Paragraph noChangelogsNote;

    @ViewComponent
    private Paragraph noAboutNote;
    @ViewComponent
    private JmixButton saveButton;

    private boolean hasSettings = false;
    @Autowired
    private DataManager dataManager;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        downloadButton.setVisible(pluginService.isPermitted("downloadPlugin"));
        removeButton.setVisible(pluginService.isPermitted("deletePlugin"));
        saveButton.setVisible(pluginService.isPermitted("savePluginConfig"));

        List<Component> settingsUI = getSettingsUI();

        if (!settingsUI.isEmpty()) {
            settingsLayout.add(settingsUI);
            noSettingsNote.setVisible(false);
            hasSettings = true;
        } else {
            hasSettings = false;
            saveButton.setTooltipText(messageBundle.getMessage("noConfigExtension"));
        }

        List<Component> aboutUI = getAboutUI();
        if (!aboutUI.isEmpty()) {
            aboutLayout.add(aboutUI);
            noAboutNote.setVisible(false);
        }

        renderChangelogs();
    }

    /**
     * Loads a plugin entity based on the given load context.
     * <p>
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
        View ownerView = this;

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

                        // Navigate back to the plugin overview after the deletion task completes.
                        viewNavigators.view(ownerView, PluginListView.class).navigate();
                    } else
                        notifications.create(messageBundle.formatMessage("pluginDeleteFailed", getEditedEntity().getId())).withType(Notifications.Type.ERROR).withPosition(Notification.Position.BOTTOM_END).show();

                }
            }).execute();
        }), new DialogAction(DialogAction.Type.CANCEL)).open();
    }

    @Subscribe(id = "termsButton", subject = "clickListener")
    public void onTermsButtonClick(final ClickEvent<JmixButton> event) {
        try {
            Optional<InstallationTermsContent> terms =
                    pluginService.getInstalledInstallationTerms(getEditedEntity().getId());
            if (terms.isEmpty()) {
                dialogs.createMessageDialog()
                        .withHeader(messageBundle.getMessage("installationTermsTitle"))
                        .withText(messageBundle.getMessage("installationTermsUnavailable"))
                        .open();
                return;
            }

            InstallationTermsContent content = terms.get();
            Dialog dialog = new Dialog();
            dialog.setHeaderTitle(content.title());
            dialog.setWidth("min(720px, 90vw)");
            dialog.setMaxHeight("85vh");

            Div scrollableTerms = new Div();
            content.components().forEach(scrollableTerms::add);
            scrollableTerms.getStyle().set("max-height", "70vh");
            scrollableTerms.getStyle().set("overflow", "auto");
            scrollableTerms.getStyle().set("padding", "var(--lumo-space-m)");

            dialog.add(scrollableTerms);
            dialog.getFooter().add(new Button(messageBundle.getMessage("installationTermsClose"), click -> dialog.close()));
            dialog.open();
        } catch (RuntimeException e) {
            log.warn("Could not open installation terms for plugin {}", getEditedEntity().getId(), e);
            dialogs.createMessageDialog()
                    .withHeader(messageBundle.getMessage("installationTermsTitle"))
                    .withText(messageBundle.getMessage("installationTermsReadFailed"))
                    .open();
        }
    }

    /**
     * Retrieves the settings UI components for the plugin.
     * <p>
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

    private List<Component> getAboutUI() {
        List<Object> extensions = pluginService.getExtension(PluginAboutExtensionPoint.class, getEditedEntity().getId());
        if (!extensions.isEmpty()) {
            try {
                List<Component> components = ((PluginAboutExtensionPoint) extensions.get(0)).render();
                if (components != null && !components.isEmpty()) {
                    return components;
                }
            } catch (RuntimeException e) {
                log.warn("Could not render About tab of plugin {}", getEditedEntity().getId(), e);
            }
        }

        return getAboutHtml();
    }

    /** Loads optional plugin-about.html from the root of the plugin JAR as an alternative to the UI extension. */
    private List<Component> getAboutHtml() {
        var wrapper = pluginService.getPlugin(getEditedEntity().getId());
        if (wrapper == null) {
            return List.of();
        }

        try (InputStream input = wrapper.getPluginClassLoader().getResourceAsStream("plugin-about.html")) {
            if (input == null) {
                return List.of();
            }
            byte[] content = input.readNBytes(MAX_ABOUT_HTML_BYTES + 1);
            if (content.length > MAX_ABOUT_HTML_BYTES) {
                log.warn("About HTML of plugin {} exceeds the {} byte limit", getEditedEntity().getId(), MAX_ABOUT_HTML_BYTES);
                noAboutNote.setText(messageBundle.getMessage("aboutHtmlTooLarge"));
                return List.of();
            }
            String html = new String(content, java.nio.charset.StandardCharsets.UTF_8);
            if (html.isBlank()) {
                return List.of();
            }
            return List.of(new Html(html));
        } catch (Exception e) {
            log.warn("Could not read About HTML of plugin {}", getEditedEntity().getId(), e);
            noAboutNote.setText(messageBundle.getMessage("aboutHtmlInvalid"));
            return List.of();
        }
    }

    /** Reads the optional plugin-changelog.json array from the plugin JAR and renders it as safe text components. */
    private void renderChangelogs() {
        var wrapper = pluginService.getPlugin(getEditedEntity().getId());
        if (wrapper == null) {
            noChangelogsNote.setVisible(true);
            return;
        }

        try (InputStream input = wrapper.getPluginClassLoader().getResourceAsStream("plugin-changelog.json")) {
            if (input == null) {
                noChangelogsNote.setVisible(true);
                return;
            }
            JsonNode entries = new ObjectMapper().readTree(input);
            if (entries == null || !entries.isArray() || entries.isEmpty()) {
                noChangelogsNote.setVisible(true);
                return;
            }

            for (JsonNode entry : entries) {
                String version = text(entry, "version", "");
                String date = text(entry, "date", "");
                String heading = version.isBlank() ? date : version + (date.isBlank() ? "" : " · " + date);
                if (!heading.isBlank()) {
                    changelogsLayout.add(new H4(heading));
                }
                JsonNode changes = entry.path("changes");
                if (changes.isArray() && !changes.isEmpty()) {
                    UnorderedList list = new UnorderedList();
                    changes.forEach(change -> {
                        String content = change.isTextual() ? change.asText() : change.toString();
                        list.add(new ListItem(content));
                    });
                    changelogsLayout.add(list);
                }
            }
            noChangelogsNote.setVisible(false);
        } catch (Exception e) {
            log.warn("Could not read changelog of plugin {}", getEditedEntity().getId(), e);
            noChangelogsNote.setText(messageBundle.getMessage("changelogsInvalid"));
            noChangelogsNote.setVisible(true);
        }
    }

    private static String text(JsonNode node, String field, String fallback) {
        JsonNode value = node.path(field);
        return value.isTextual() ? value.asText() : fallback;
    }

    /**
     * Retrieves the plugin configuration extension instance for the currently edited entity.
     * <p>
     * This method fetches an instance of the {@link PluginConfigExtensionPoint} associated with
     * the plugin identified by the ID of the currently edited entity and retrieves the first
     * extension available for the given plugin.
     *
     * @return an instance of {@link PluginConfigExtensionPoint
     */
    private PluginConfigExtensionPoint getPluginConfig() {
        List<Object> objectsIO = pluginService.getExtension(PluginConfigExtensionPoint.class, getEditedEntity().getId());

        if (objectsIO.isEmpty())
            return null;
        else
            return (PluginConfigExtensionPoint) objectsIO.get(0);
    }


    @Subscribe("pluginTabSheet")
    public void onPluginTabSheetSelectedChange(final JmixTabSheet.SelectedChangeEvent event) {
        log.info("Plugin tab sheet selected change event fired. Selected tab ID: {}", event.getSelectedTab().getId());

        if (hasSettings) {
            Optional<String> selectedTab = event.getSelectedTab().getId();

            if (selectedTab.isPresent())
                saveButton.setEnabled(selectedTab.get().equals("settings"));
        }
    }

    @Subscribe(id = "saveButton", subject = "clickListener")
    public void onSaveButtonClick(final ClickEvent<JmixButton> event) {
        ((PluginConfigExtensionPoint) pluginService.getExtension(PluginConfigExtensionPoint.class, getEditedEntity().getId()).get(0)).save();

        notifications.create(messageBundle.formatMessage("pluginConfigSaved", getEditedEntity().getId())).withType(Notifications.Type.SUCCESS).withPosition(Notification.Position.BOTTOM_END).show();
    }


}
