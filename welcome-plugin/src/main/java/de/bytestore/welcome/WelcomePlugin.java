package de.bytestore.welcome;

import de.bytestore.plugin.JmixPlugin;
import de.bytestore.welcome.extensions.ConfigExtension;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * WelcomePlugin extends the functionality of the JmixPlugin to add custom behavior
 * and lifecycle management for the plugin. It uses the PF4J framework and
 * integrates with the Jmix application context to provide seamless plugin operations.
 *
 * Responsibilities:
 * - Initializes the plugin with the provided metadata and lifecycle requirements.
 * - Implements start and stop methods to customize the plugin's lifecycle behavior.
 */
@Component
public class WelcomePlugin extends JmixPlugin {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructs a WelcomePlugin instance with the provided PluginWrapper.
     * This constructor initializes the plugin by passing the wrapper to
     * the superclass, which sets up the necessary context and plugin framework
     * requirements specific to its lifecycle.
     *
     * @param wrapper the PluginWrapper instance that contains information
     *                about the plugin, such as its metadata, class loader,
     *                and other attributes needed for initialization.
     */
    public WelcomePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Starts the lifecycle of the WelcomePlugin by performing any necessary initialization tasks.
     * This method logs the start of the plugin and checks the value of the "welcome.debug" property
     * using the `pluginService`. If the debug mode is enabled (as determined by the property value),
     * an additional log message is generated to indicate that debug mode is active.
     *
     * Overrides the `start` method from the parent class to provide specific behavior
     * for the WelcomePlugin.
     *
     * Responsibilities:
     * - Logs the start of the WelcomePlugin.
     * - Checks the "welcome.debug" property and logs the debug mode status if enabled.
     *
     * No parameters are required. This method does not return any value.
     */
    @Override
    public void start() {
        log.info("WelcomePlugin.start()");

//        if ((Boolean) pluginService.getValue("welcome.debug", false))
//            log.info("Debug Mode is enabled.");
    }

    /**
     * Stops the lifecycle of the WelcomePlugin by performing necessary cleanup tasks.
     * This method is invoked when the plugin is being stopped or deactivated.
     * Logs the stop action for diagnostic or informational purposes.
     *
     * Overrides the `stop` method from the parent class to provide specific stop behavior
     * for the WelcomePlugin.
     *
     * Responsibilities:
     * - Logs the stop action for the WelcomePlugin.
     *
     * No parameters are required. This method does not return any value.
     */
    @Override
    public void stop() {
        log.info("WelcomePlugin.stop()");
    }

    /**
     * Creates and initializes the application context specific to the WelcomePlugin.
     * This method configures a Spring `AnnotationConfigApplicationContext` with a custom
     * class loader, registers additional plugin-specific extensions, and scans a base
     * package for components. Once everything is configured, the context is refreshed
     * to complete its initialization.
     *
     * @return the fully initialized Spring `ApplicationContext` for the WelcomePlugin.
     */
    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(ConfigExtension.class);
        applicationContext.scan("de.bytestore.welcome");

        applicationContext.refresh();

        return applicationContext;
    }
}