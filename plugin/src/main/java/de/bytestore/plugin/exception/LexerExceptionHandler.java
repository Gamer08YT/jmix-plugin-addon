package de.bytestore.plugin.exception;

import com.vaadin.flow.component.notification.Notification;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.exception.AbstractUiExceptionHandler;
import io.jmix.flowui.view.MessageBundle;
import org.springframework.stereotype.Component;

@Component
public class LexerExceptionHandler extends AbstractUiExceptionHandler {
    private final Notifications notifications;
    private final MessageBundle messageBundle;

    public LexerExceptionHandler(Notifications notifications, MessageBundle messageBundle) {
        super(LexerExceptionHandler.class.getName());
        this.notifications = notifications;
        this.messageBundle = messageBundle;

        this.messageBundle.setMessageGroup("de.bytestore.plugin.exception");
    }

    @Override
    protected void doHandle(String className, String message, Throwable throwable) {
        notifications.create(messageBundle.getMessage("pluginError"), messageBundle.getMessage("pluginWrongVersionFormat")).withPosition(Notification.Position.BOTTOM_END).withType(Notifications.Type.ERROR).show();
    }
}
