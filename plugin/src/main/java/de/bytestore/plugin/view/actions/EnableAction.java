package de.bytestore.plugin.view.actions;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.bytestore.plugin.service.PluginService;
import io.jmix.core.Messages;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.action.ActionType;
import io.jmix.flowui.action.list.ItemTrackingAction;
import io.jmix.flowui.view.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;

@ActionType("enablePlugin")
public class EnableAction<Plugin> extends ItemTrackingAction<de.bytestore.plugin.entity.Plugin> {
    private MessageBundle messageBundle;

    @Autowired
    private Notifications notifications;

    @Autowired
    private PluginService pluginService;

    public EnableAction(String id) {
        super(id);
        //setText(messageBundle.getMessage("enable"));
        setIcon(VaadinIcon.UNLOCK.create());
    }

    @Autowired
    protected void setMessages(MessageBundle messageBundle) {
        this.messageBundle = messageBundle;
        this.messageBundle.setMessageGroup("de.bytestore.plugin.view.actions");

        setText(messageBundle.getMessage("enable"));
    }


    @Override
    public void actionPerform(Component componentIO) {
        if (getTarget() != null) {
            de.bytestore.plugin.entity.Plugin selectedIO = getTarget().getSingleSelectedItem();

            if (selectedIO != null) {
                if (pluginService.enablePlugin(selectedIO.getId()))
                    notifications.create(messageBundle.formatMessage("pluginEnabled", selectedIO.getId())).withType(Notifications.Type.SUCCESS).show();
                else
                    notifications.create(messageBundle.formatMessage("pluginEnableFailed", selectedIO.getId())).withType(Notifications.Type.ERROR).show();
            }
        }
    }
}
