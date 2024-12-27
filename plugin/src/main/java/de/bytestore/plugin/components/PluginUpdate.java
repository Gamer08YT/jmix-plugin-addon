package de.bytestore.plugin.components;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import de.bytestore.plugin.service.UpdateService;
import io.jmix.core.Messages;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.action.DialogAction;
import io.jmix.flowui.backgroundtask.BackgroundTask;
import io.jmix.flowui.backgroundtask.TaskLifeCycle;
import io.jmix.flowui.view.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

@Tag("plugin-update")
public class PluginUpdate extends Composite<Button> {
//    @Autowired
//    private UpdateService updateService;
//
//    @Autowired
//    private Messages messages;
//
//    @Autowired
//    private Notifications notifications;

    @Autowired
    private Dialogs dialogs;

    public PluginUpdate() {

    }

    /**
     * Called when the content of this composite is requested for the first
     * time.
     * <p>
     * This method should initialize the component structure for the composite
     * and return the root component.
     * <p>
     * By default, this method uses reflection to instantiate the component
     * based on the generic type of the sub class.
     *
     * @return the root component which this composite wraps, never {@code null}
     */
    @Override
    protected Button initContent() {
        Button buttonIO = super.initContent();

//        buttonIO.setText(
//                messageBundle.getMessage("update"));
        buttonIO.setIcon(VaadinIcon.REFRESH.create());
//        buttonIO.addClickListener(event -> {
//            dialogs.createOptionDialog().withHeader(messageBundle.getMessage("update")).withText(messages.formatMessage("de.bytestore.plugin.view.plugin/updateWarning", plugin.getId())).withActions(new DialogAction(DialogAction.Type.YES).withHandler(actionPerformedEvent -> {
//                backgroundWorker.handle(new BackgroundTask<Boolean, Boolean>(TimeUnit.MINUTES.toSeconds(1)) {
//                    @Override
//                    public Boolean run(TaskLifeCycle<Boolean> taskLifeCycle) throws Exception {
//                        return updateService.updateAll();
//                    }
//
//                    /**
//                     * Called by the execution environment in UI thread when the task is completed.
//                     *
//                     * @param result result of execution returned by {@link #run(TaskLifeCycle)} method
//                     */
//                    @Override
//                    public void done(Boolean result) {
//                        if (result != null && result.booleanValue()) {
//                            notifications.create(messageBundle.formatMessage("pluginUpdated", plugin.getId(), releaseIO.version)).withType(Notifications.Type.SUCCESS).withPosition(Notification.Position.BOTTOM_END).show();
//
//                        } else
//                            notifications.create(messageBundle.formatMessage("pluginUpdateFailed", plugin.getId())).withType(Notifications.Type.ERROR).withPosition(Notification.Position.BOTTOM_END).show();
//
//                    }
//                }).execute();
//
//            }), new DialogAction(DialogAction.Type.CANCEL)).open();
//
//            updateService.updateAll();
//        });

        return buttonIO;
    }
}
