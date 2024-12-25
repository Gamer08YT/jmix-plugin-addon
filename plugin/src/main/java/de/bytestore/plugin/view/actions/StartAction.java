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

@ActionType("startPlugin")
public class StartAction<E> extends ItemTrackingAction<E> {
    private static final Logger log = LoggerFactory.getLogger(StartAction.class);

    private MessageBundle messageBundle;

    @Autowired
    private Notifications notifications;

    @Autowired
    private PluginService pluginService;

    public StartAction(String id) {
        super(id);
        //setText(messageBundle.getMessage("start"));
        setIcon(VaadinIcon.PLAY.create());
        setVisibleByUiPermissions(true);
    }

    @Autowired
    protected void setMessages(MessageBundle messageBundle) {
        this.messageBundle = messageBundle;
        this.messageBundle.setMessageGroup("de.bytestore.plugin.view.actions");

        setText(messageBundle.getMessage("start"));
    }

    @Override
    protected void onSelectionChange(SelectionEvent<?, E> event) {
        super.onSelectionChange(event);

        event.getFirstSelectedItem().ifPresent(item -> {
            if (item instanceof de.bytestore.plugin.entity.Plugin) {
                setEnabled(((de.bytestore.plugin.entity.Plugin) item).getState() != PluginState.STARTED);
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
                    notifications.create(messageBundle.formatMessage("pluginStarted", selectedIO.getId())).withType(Notifications.Type.SUCCESS).withPosition(Notification.Position.BOTTOM_END).show();
                } catch (Exception e) {
                    notifications.create(messageBundle.formatMessage("pluginStartFailed", selectedIO.getId())).withType(Notifications.Type.ERROR).withPosition(Notification.Position.BOTTOM_END).show();

                    log.error("Unable to Start Plugin: {}.", selectedIO.getId(), e);
                }
            }
        }
    }
}
