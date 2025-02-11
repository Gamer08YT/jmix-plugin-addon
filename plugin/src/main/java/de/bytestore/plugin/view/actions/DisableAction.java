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
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The DisableAction class represents an action to disable a specific plugin
 * within the application. This action can be performed on an item selection
 * within the user interface and utilizes services for plugin management
 * and notifications to handle the plugin's state and provide user feedback.
 *
 * @param <E> The type of items this action is applied to.
 *
 * Annotations:
 * - {@code @ActionType("disablePlugin")}: Identifies this action with the type "disablePlugin".
 */
@ActionType("disablePlugin")
public class DisableAction<E> extends ItemTrackingAction<E> {
    private MessageBundle messageBundle;

    @Autowired
    private Notifications notifications;

    @Autowired
    private PluginService pluginService;

    @Autowired
    protected void setMessages(MessageBundle messageBundle) {
        this.messageBundle = messageBundle;
        this.messageBundle.setMessageGroup("de.bytestore.plugin.view.actions");

        setText(messageBundle.getMessage("disable"));
    }


    public DisableAction(String id) {
        super(id);
        setIcon(VaadinIcon.LOCK.create());
        setVisibleByUiPermissions(true);
    }

    @Override
    protected void onSelectionChange(SelectionEvent<?, E> event) {
        super.onSelectionChange(event);

        event.getFirstSelectedItem().ifPresent(item -> {
            if (item instanceof de.bytestore.plugin.entity.Plugin) {
                setEnabled(((Plugin) item).getState() != PluginState.DISABLED);
            }
        });
    }

    @Override
    public void actionPerform(Component componentIO) {
        if (getTarget() != null) {
            Plugin selectedIO = (de.bytestore.plugin.entity.Plugin) getTarget().getSingleSelectedItem();

            if (selectedIO != null) {
                if (pluginService.disablePlugin(selectedIO.getId()))
                    notifications.create(messageBundle.formatMessage("pluginDisabled", selectedIO.getId())).withType(Notifications.Type.SUCCESS).withPosition(Notification.Position.BOTTOM_END).show();
                else
                    notifications.create(messageBundle.formatMessage("pluginDisableFailed", selectedIO.getId())).withType(Notifications.Type.ERROR).withPosition(Notification.Position.BOTTOM_END).show();
            }
        }
    }
}
