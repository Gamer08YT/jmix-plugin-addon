package de.bytestore.plugin.view.actions;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.bytestore.plugin.service.PluginService;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.action.ActionType;
import io.jmix.flowui.action.list.ItemTrackingAction;
import io.jmix.flowui.view.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;

@ActionType("disablePlugin")
public class DisableAction<Plugin> extends ItemTrackingAction<de.bytestore.plugin.entity.Plugin> {
    @Autowired
    private MessageBundle messageBundle;

    @Autowired
    private Notifications notifications;

    @Autowired
    private PluginService pluginService;

    public DisableAction(String id) {
        super(id);
        setText(messageBundle.getMessage("disable"));
        setIcon(VaadinIcon.LOCK.create());
    }

    @Override
    public void actionPerform(Component componentIO) {
        if (getTarget() != null) {
            de.bytestore.plugin.entity.Plugin selectedIO = getTarget().getSingleSelectedItem();

            if (selectedIO != null) {
                if (pluginService.disablePlugin(selectedIO.getId()))
                    notifications.create(messageBundle.formatMessage("pluginDisabled", selectedIO.getId())).withType(Notifications.Type.SUCCESS).show();
                else
                    notifications.create(messageBundle.formatMessage("pluginDisableFailed", selectedIO.getId())).withType(Notifications.Type.ERROR).show();
            }
        }
    }
}
