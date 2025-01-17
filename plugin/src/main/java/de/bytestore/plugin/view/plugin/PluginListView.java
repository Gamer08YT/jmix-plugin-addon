package de.bytestore.plugin.view.plugin;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Plugin;
import de.bytestore.plugin.entity.PluginState;
import de.bytestore.plugin.service.PluginService;
import de.bytestore.plugin.service.UpdateService;
import de.bytestore.plugin.view.pluginupload.PluginUploadView;
import io.jmix.core.LoadContext;
import io.jmix.core.Messages;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.action.DialogAction;
import io.jmix.flowui.backgroundtask.BackgroundTask;
import io.jmix.flowui.backgroundtask.BackgroundWorker;
import io.jmix.flowui.backgroundtask.TaskLifeCycle;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.pf4j.update.PluginInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Route(value = "plugins", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_Plugin.list")
@ViewDescriptor(path = "plugin-list-view.xml")
@LookupComponent("pluginsDataGrid")
@DialogMode(width = "50em")
public class PluginListView extends StandardListView<Plugin> {

    @Autowired
    private PluginService pluginService;

    @ViewComponent
    private DataGrid<Plugin> pluginsDataGrid;

    @Autowired
    private Messages messages;

    @Autowired
    private Notifications notifications;

    @ViewComponent
    private MessageBundle messageBundle;

    @Autowired
    private UpdateService updateService;

    @ViewComponent
    private JmixButton disableButton;

    @ViewComponent
    private JmixButton enableButton;

    @ViewComponent
    private JmixButton startButton;

    @ViewComponent
    private JmixButton stopButton;

    @Autowired
    private Dialogs dialogs;

    @Autowired
    private BackgroundWorker backgroundWorker;
    @Autowired
    private ViewNavigators viewNavigators;
    @Autowired
    private DialogWindows dialogWindows;

    /**
     * Initializes the plugin list view by configuring the plugins data grid and adding component columns
     * for displaying and interacting with plugin versions, states, and required version information.
     *
     * This method is triggered by the {@link InitEvent} of the view lifecycle and performs the following:
     * - Adds a version column displaying the plugin's version status, including update availability.
     * - Adds a state column showing the current state of each plugin with a corresponding badge color.
     * - Adds a required version column if version check is enabled, showing version compatibility information.
     *
     * @param event the initialization event for the view, providing context for configuring the UI components
     */
    @Subscribe
    public void onInit(final InitEvent event) {
        // Add Version Badge.
        Grid.Column<Plugin> versionIO = pluginsDataGrid.addComponentColumn(plugin -> {
            String colorIO = "success";
            Span spanIO = new Span();

            // Add Badge Theme.
            spanIO.getElement().getThemeList().add("badge");

            if (updateService.isUpdateAvailable(plugin) && pluginService.isPermitted("update")) {
                spanIO.setText(messages.getMessage("updateAvailable"));
                colorIO = "warning";

                PluginInfo.PluginRelease releaseIO = updateService.getLastRelease(plugin);

                // Add Update Tooltip.
                Tooltip.forComponent(spanIO).withText(messageBundle.formatMessage("updateAvailableTooltip", plugin.getId(), plugin.getVersion(), releaseIO.version));

                // Update Plugin via Background Worker.
                spanIO.addClickListener(clickEvent -> {
                    dialogs.createOptionDialog().withHeader(messageBundle.getMessage("update")).withText(messageBundle.formatMessage("updateWarning", plugin.getId())).withActions(new DialogAction(DialogAction.Type.YES).withHandler(actionPerformedEvent -> {
                        backgroundWorker.handle(new BackgroundTask<Boolean, Boolean>(TimeUnit.MINUTES.toSeconds(1)) {
                            @Override
                            public Boolean run(TaskLifeCycle<Boolean> taskLifeCycle) throws Exception {
                                return updateService.update(plugin);
                            }

                            /**
                             * Called by the execution environment in UI thread when the task is completed.
                             *
                             * @param result result of execution returned by {@link #run(TaskLifeCycle)} method
                             */
                            @Override
                            public void done(Boolean result) {
                                if (result != null && result.booleanValue()) {
                                    notifications.create(messageBundle.formatMessage("pluginUpdated", plugin.getId(), releaseIO.version)).withType(Notifications.Type.SUCCESS).withPosition(Notification.Position.BOTTOM_END).show();

                                    getUI().ifPresent(ui -> ui.access(() -> {
                                        pluginsDataGrid.getDataProvider().refreshItem(plugin);
                                    }));
                                } else
                                    notifications.create(messageBundle.formatMessage("pluginUpdateFailed", plugin.getId())).withType(Notifications.Type.ERROR).withPosition(Notification.Position.BOTTOM_END).show();

                            }
                        }).execute();

                    }), new DialogAction(DialogAction.Type.CANCEL)).open();

                });
            } else {
                spanIO.setText(plugin.getVersion());

                // Add Up-To-Date Tooltip.
                Tooltip.forComponent(spanIO).withText(messageBundle.getMessage("upToDate"));
            }

            // Add Badge Color.
            spanIO.getElement().getThemeList().add(colorIO);

            return spanIO;
        }).setHeader(messageBundle.getMessage("version"));

        pluginsDataGrid.setColumnPosition(versionIO, 1);

        // Add State Badge.
        pluginsDataGrid.addComponentColumn(plugin -> {
            Span spanIO = new Span(messages.getMessage(plugin.getState()));

            // Add Badge Theme.
            spanIO.getElement().getThemeList().add("badge");

            // Set Badge Color.
            spanIO.getElement().getThemeList().add(getStateColor(plugin.getState()));

            return spanIO;
        }).setHeader(messageBundle.getMessage("state"));


        // Show Version Status if Version Check is enabled.
        if (updateService.isVersionCheck()) {
            pluginsDataGrid.addComponentColumn(plugin -> {
                String colorIO = "error";
                Span spanIO = new Span();

                // Add Badge Theme.
                spanIO.getElement().getThemeList().add("badge");

                if (!plugin.getRequires().isEmpty()) {
                    // Add Description Tooltip.
                    Tooltip.forComponent(spanIO).withText(messageBundle.formatMessage("requiredVersion", plugin.getRequires(), updateService.getVersion()));

                    // Set Min Version.
                    spanIO.setText(messages.getMessage(plugin.getRequires()));

                    // Check if the Plugin is outdated.
                    colorIO = (pluginService.isOutdated(plugin) ? "error" : "success");

                } else {
                    colorIO = "success";

                    spanIO.setText(messageBundle.getMessage("requiredVersionNone"));
                }

                // Add Badge Color.
                spanIO.getElement().getThemeList().add(colorIO);

                return spanIO;
            }).setHeader(messages.getMessage("required"));
        }
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        setActionsVisibility();
        setButtonVisibility();
    }

    /**
     * Configures the visibility of action buttons in the UI based on the current user's permissions.
     *
     * This method uses the {@code pluginService.isPermitted} logic to determine if the user
     * has access to specific actions and adjusts the visibility of the corresponding buttons.
     *
     * Buttons affected by this method include:
     * - {@code disableButton}: Determines if the "Disable" action is permitted.
     * - {@code enableButton}: Determines if the "Enable" action is permitted.
     * - {@code startButton}: Determines if the "Start" action is permitted.
     * - {@code stopButton}: Determines if the "Stop" action is permitted.
     *
     * The visibility of each button is updated accordingly.
     */
    private void setButtonVisibility() {
        disableButton.setVisible(pluginService.isPermitted("disable"));
        enableButton.setVisible(pluginService.isPermitted("enable"));
        startButton.setVisible(pluginService.isPermitted("start"));
        stopButton.setVisible(pluginService.isPermitted("stop"));
    }

    /**
     * Configures the visibility of specific actions for the plugins data grid
     * based on the current user's permissions.
     *
     * This method uses the `pluginService.isPermitted` logic to determine*/
    private void setActionsVisibility() {
        pluginsDataGrid.getAction("disable").setVisible(pluginService.isPermitted("disable"));
        pluginsDataGrid.getAction("enable").setVisible(pluginService.isPermitted("enable"));
        pluginsDataGrid.getAction("start").setVisible(pluginService.isPermitted("start"));
        pluginsDataGrid.getAction("stop").setVisible(pluginService.isPermitted("stop"));
    }


    /**
     * Determines the color theme associated with a given {@link PluginState}.
     * Each {@code PluginState} maps to a specific string representation of a theme color.
     *
     * @param stateIO the state of the plugin for which a corresponding color is required.
     * @return a string representing the color associated with the provided {@code PluginState}.
     *         Returns "success" for STARTED, "error" for FAILED, "contrast" for DISABLED,
     *         and "normal" for any other states.
     */
    private String getStateColor(PluginState stateIO) {
        switch (stateIO) {
            case STARTED -> {
                return "success";
            }
            case FAILED -> {
                return "error";
            }
            case DISABLED -> {
                return "contrast";
            }
            default -> {
                return "normal";
            }
        }
    }

    @Install(to = "pluginsDl", target = Target.DATA_LOADER)
    protected List<Plugin> pluginsDlLoadDelegate(LoadContext<Plugin> loadContext) {
        // Here you can load entities from an external storage.
        // Set the loaded entities to the not-new state using EntityStates.setNew(entity, false).
        return pluginService.castPlugins();
    }

    @Subscribe(id = "reloadButton", subject = "clickListener")
    public void onReloadButtonClick(final ClickEvent<JmixButton> event) {
        dialogs.createOptionDialog().withHeader(messageBundle.getMessage("reload")).withText(messageBundle.getMessage("reloadWarning")).withActions(new DialogAction(DialogAction.Type.YES).withHandler(actionPerformedEvent -> {
            backgroundWorker.handle(new BackgroundTask<Integer, Void>(TimeUnit.MINUTES.toSeconds(1)) {
                @Override
                public Void run(TaskLifeCycle<Integer> taskLifeCycle) throws Exception {
                    pluginService.reload();

                    return null;
                }

                /**
                 * Called by the execution environment in UI thread when the task is completed.
                 *
                 * @param result result of execution returned by {@link #run(TaskLifeCycle)} method
                 */
                @Override
                public void done(Void result) {
                    notifications.create(messageBundle.getMessage("pluginsReloaded")).withType(Notifications.Type.SUCCESS).withPosition(Notification.Position.BOTTOM_END).show();

                    getUI().ifPresent(ui -> ui.access(() -> {
                        pluginsDataGrid.getDataProvider().refreshAll();
                    }));
                }
            }).execute();

        }), new DialogAction(DialogAction.Type.CANCEL)).open();
    }

    /**
     * Handles the click event of the upload button in the plugin list view.
     *
     * When the upload button is clicked, this method opens the {@link PluginUploadView} dialog window.
     * After the dialog is closed, it refreshes the plugins data grid to reflect any changes made during the upload process.
     *
     * @param event the click event associated with the upload button, containing details about the source button and click context
     */
    @Subscribe(id = "uploadButton", subject = "clickListener")
    public void onUploadButtonClick(final ClickEvent<JmixButton> event) {
        dialogWindows.view(this, PluginUploadView.class).withAfterCloseListener(pluginUploadViewAfterCloseEvent -> {
            pluginsDataGrid.getDataProvider().refreshAll();
        });
    }


}
