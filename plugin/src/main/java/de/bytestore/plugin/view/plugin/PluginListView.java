package de.bytestore.plugin.view.plugin;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Plugin;
import de.bytestore.plugin.entity.PluginState;
import de.bytestore.plugin.service.PluginService;
import de.bytestore.plugin.service.UpdateService;
import io.jmix.core.LoadContext;
import io.jmix.core.Messages;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.component.button.JmixButton;
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

    @ViewComponent
    private DataGrid<Plugin> pluginsDataGrid;

    @Autowired
    private Messages messages;

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

    @Subscribe
    public void onInit(final InitEvent event) {
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
}
