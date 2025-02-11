package de.bytestore.plugin.exception;

import com.vaadin.flow.component.notification.Notification;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.exception.AbstractUiExceptionHandler;
import io.jmix.flowui.view.MessageBundle;
import org.pf4j.PluginNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Handles exceptions specifically related to the non-existence of plugins within the application UI.
 * This class extends {@link AbstractUiExceptionHandler} to manage UI-based exception notifications.
 *
 * The exception handler utilizes the {@link Notifications} and {@link MessageBundle} components
 * to create user-friendly error messages and display them effectively in the UI.
 *
 * The primary purpose of this handler is to intercept instances of {@link PluginNotFoundException},
 * format appropriate error messages, and deliver user notifications to inform about missing plugins.
 *
 * Dependencies injected:
 * - Notifications: Used for creating and displaying notifications in the application UI.
 * - MessageBundle: Used for fetching locale-specific messages related to plugin exceptions.
 *
 * This class provides specific feedback about plugin-related issues to improve the user's understanding
 * of errors when a requested plugin cannot be found.
 */
@Component
public class PluginNotFoundExceptionHandler extends AbstractUiExceptionHandler {
    private final Notifications notifications;
    private final MessageBundle messageBundle;

    public PluginNotFoundExceptionHandler(Notifications notifications, MessageBundle messageBundle) {
        super(PluginNotFoundException.class.getName());
        this.notifications = notifications;
        this.messageBundle = messageBundle;

        this.messageBundle.setMessageGroup("de.bytestore.plugin.exception");
    }

    @Override
    protected void doHandle(String className, String message, Throwable throwable) {
        notifications.create(messageBundle.getMessage("pluginError"), messageBundle.getMessage("pluginNotFound")).withPosition(Notification.Position.BOTTOM_END).withType(Notifications.Type.ERROR).show();
    }
}
