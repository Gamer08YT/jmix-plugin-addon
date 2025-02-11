package de.bytestore.plugin.exception;

import com.vaadin.flow.component.notification.Notification;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.exception.AbstractUiExceptionHandler;
import io.jmix.flowui.view.MessageBundle;
import org.springframework.stereotype.Component;

/**
 * Handles exceptions specifically related to lexer processes within the application UI.
 * This class extends {@link AbstractUiExceptionHandler} to manage
 * UI-based exception notifications.
 *
 * The exception handler utilizes the {@link Notifications} and
 * {@link MessageBundle} components to create user-friendly error messages
 * and display them in the UI.
 *
 * The focus of this handler is to intercept lexer-related exceptions, format relevant
 * error messages, and deliver appropriate notifications to the user.
 *
 * Dependencies injected:
 * - Notifications: Used for creating and managing UI notifications.
 * - MessageBundle: Used for loading locale-specific messages related to lexer exceptions.
 *
 * The class differentiates lexer errors, providing specific user feedback for issues such
 * as format-related errors in version specifications.
 */
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
