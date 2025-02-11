package de.bytestore.plugin.view.actions;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.selection.SelectionEvent;
import de.bytestore.plugin.entity.Plugin;
import de.bytestore.plugin.entity.PluginState;
import de.bytestore.plugin.service.PluginService;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.action.ActionType;
import io.jmix.flowui.action.list.ItemTrackingAction;
import io.jmix.flowui.view.MessageBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The StopAction class represents an action that stops a plugin in the system.
 * This action checks the state of the plugin to ensure it is in the STARTED state
 * before allowing the stop operation. It displays notifications for both success
 * and error scenarios and logs any errors encountered during the process.
 *
 * @param <E> the type of the items tracked by this action
 *
 * Annotations:
 * - @ActionType: Represents the action type, defined as "stopPlugin".
 *
 * Dependencies:
 * - Logger: Used for logging error messages.
 * - MessageBundle: Handles localization and message configuration.
 * - Notifications: Manages user notifications for success or failure messages.
 * - PluginService: Manages operations on plugins, including stopping them.
 *
 * Constructor:
 * - StopAction(String id): Initializes the action with an identifier and sets the default
 *   text and icon for the action button.
 *
 * Methods:
 * - setMessages(MessageBundle messageBundle): Configures the message bundle and localizes
 *   the action's text. Also enables visibility based on UI permissions.
 * - onSelectionChange(SelectionEvent<?, E> event): Updates the enabled state of the action
 *   based on the selected item's state, ensuring the action is available only if the plugin
 *   is in the STARTED state.
 * - actionPerform(Component componentIO): Performs the stop operation on the selected plugin.
 *   It communicates with the PluginService to stop the plugin and shows a notification based
 *   on the result of the operation. Logs an error message if the operation fails.
 */
@ActionType("stopPlugin")
public class StopAction<E> extends ItemTrackingAction<E> {
    private static final Logger log = LoggerFactory.getLogger(StopAction.class);

    private MessageBundle messageBundle;

    @Autowired
    private Notifications notifications;

    @Autowired
    private PluginService pluginService;

    public StopAction(String id) {
        super(id);
        //setText(messageBundle.getMessage("stop"));
        setIcon(VaadinIcon.STOP.create());
    }

    @Autowired
    protected void setMessages(MessageBundle messageBundle) {
        this.messageBundle = messageBundle;
        this.messageBundle.setMessageGroup("de.bytestore.plugin.view.actions");

        setText(messageBundle.getMessage("stop"));
        setVisibleByUiPermissions(true);
    }

    @Override
    protected void onSelectionChange(SelectionEvent<?, E> event) {
        super.onSelectionChange(event);

        event.getFirstSelectedItem().ifPresent(item -> {
            if (item instanceof de.bytestore.plugin.entity.Plugin) {
                setEnabled(((de.bytestore.plugin.entity.Plugin) item).getState() == PluginState.STARTED);
            }
        });
    }


    @Override
    public void actionPerform(Component componentIO) {
        if (getTarget() != null) {
            de.bytestore.plugin.entity.Plugin selectedIO = (Plugin) getTarget().getSingleSelectedItem();

            if (selectedIO != null) {
                try {
                    pluginService.stopPlugin(selectedIO.getId());
                    notifications.create(messageBundle.formatMessage("pluginStopped", selectedIO.getId())).withType(Notifications.Type.SUCCESS).withPosition(Notification.Position.BOTTOM_END).show();
                } catch (Exception e) {
                    notifications.create(messageBundle.formatMessage("pluginStopFailed", selectedIO.getId())).withType(Notifications.Type.ERROR).withPosition(Notification.Position.BOTTOM_END).show();

                    log.error("Unable to Stop Plugin: {}.", selectedIO.getId(), e);
                }
            }
        }
    }
}
