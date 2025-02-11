package de.bytestore.plugin.view.actions;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.selection.SelectionEvent;
import de.bytestore.plugin.entity.PluginState;
import de.bytestore.plugin.service.PluginService;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.action.ActionType;
import io.jmix.flowui.action.list.ItemTrackingAction;
import io.jmix.flowui.view.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The EnableAction class provides the functionality to enable a plugin within an application.
 * It extends the ItemTrackingAction class and incorporates functionalities for interaction
 * with plugin services, notification handling, and UI-based enablement controls based on permissions.
 *
 * This action is associated with setting a specific plugin to an enabled state. The class checks
 * the current state of a plugin and ensures only disabled plugins can be enabled. Notifications
 * are used to inform the user about the success or failure of the enablement action.
 *
 * @param <E> the type of entity handled by this action
 */
@ActionType("enablePlugin")
public class EnableAction<E> extends ItemTrackingAction<E> {
    private MessageBundle messageBundle;

    @Autowired
    private Notifications notifications;

    @Autowired
    private PluginService pluginService;

    public EnableAction(String id) {
        super(id);
        //setText(messageBundle.getMessage("enable"));
        setIcon(VaadinIcon.UNLOCK.create());
        setVisibleByUiPermissions(true);
    }

    @Autowired
    protected void setMessages(MessageBundle messageBundle) {
        this.messageBundle = messageBundle;
        this.messageBundle.setMessageGroup("de.bytestore.plugin.view.actions");

        setText(messageBundle.getMessage("enable"));
    }

    @Override
    protected void onSelectionChange(SelectionEvent<?, E> event) {
        super.onSelectionChange(event);

        event.getFirstSelectedItem().ifPresent(item -> {
            if (item instanceof de.bytestore.plugin.entity.Plugin) {
                setEnabled(((de.bytestore.plugin.entity.Plugin) item).getState() == PluginState.DISABLED);
            }
        });
    }


    @Override
    public void actionPerform(Component componentIO) {
        if (getTarget() != null) {
            de.bytestore.plugin.entity.Plugin selectedIO = (de.bytestore.plugin.entity.Plugin) getTarget().getSingleSelectedItem();

            if (selectedIO != null) {
                if (pluginService.enablePlugin(selectedIO.getId()))
                    notifications.create(messageBundle.formatMessage("pluginEnabled", selectedIO.getId())).withType(Notifications.Type.SUCCESS).withPosition(Notification.Position.BOTTOM_END).show();
                else
                    notifications.create(messageBundle.formatMessage("pluginEnableFailed", selectedIO.getId())).withType(Notifications.Type.ERROR).withPosition(Notification.Position.BOTTOM_END).show();
            }
        }
    }
}
