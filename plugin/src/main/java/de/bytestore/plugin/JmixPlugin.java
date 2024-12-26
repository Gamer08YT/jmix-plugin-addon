package de.bytestore.plugin;

import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Represents a Jmix plugin built on top of the SpringPlugin implementation.
 * This class is responsible for extending the functionality of the
 * PF4J (Plugin Framework for Java) plugin system by adding support
 * for creating a Spring-based application context.
 *
 * The JmixPlugin class integrates with the Jmix framework, enabling
 * seamless interaction with other Jmix modules and configurations.
 *
 * Responsibilities:
 * - Initializes the plugin with a provided PluginWrapper instance.
 * - Overrides the method to create a Spring application context for the plugin.
 *
 * Note: The actual application context creation logic should be implemented
 * in place of returning a null value in the overridden method.
 */
public class JmixPlugin extends SpringPlugin {
    // Store Annotation Config Context.
    private final AnnotationConfigApplicationContext contextIO = new AnnotationConfigApplicationContext();

    public JmixPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Creates and returns the application context for the plugin. This allows
     * the plugin to establish its own Spring application context, enabling
     * dependency injection, bean management, and integration with the Jmix framework.
     * The method must be implemented to provide the actual application context
     * relevant to the plugin's functionality.
     *
     * @return the application context specific to the plugin, or null if the
     *         application context creation is not implemented.
     */
    @Override
    protected ApplicationContext createApplicationContext() {
        contextIO.setClassLoader(getWrapper().getPluginClassLoader());

        return contextIO;
    }

    /**
     * Registers the provided class into the application context and refreshes the context.
     * This method allows for dynamically adding new configurations or beans to the plugin's
     * Spring application context during runtime.
     *
     * @param classIO the class to be registered within the application context. It is expected
     *                to define configurations, beans, or other components necessary for
     *                the plugin's functionality.
     */
    public void register(Class classIO) {
        contextIO.register(classIO);
        contextIO.refresh();
    }

    /**
     * Scans the specified base package and refreshes the application context.
     * This method utilizes the `scan` functionality of the Spring
     * `AnnotationConfigApplicationContext` to load components, beans, or configurations
     * from the provided package into the application context.
     *
     * @param packageIO the base package to be scanned for components and configurations.
     */
    public void scan(String packageIO) {
        contextIO.scan(packageIO);
        contextIO.refresh();
    }

    /**
     * Stops the plugin's lifecycle by invoking the necessary cleanup logic.
     * This method overrides the default stop behavior in the parent class
     * and ensures that any resources held by the plugin are properly
     * released or closed.
     *
     * Subclasses can extend this method to include additional cleanup
     * procedures specific to the plugin's functionality.
     */
    @Override
    public void stop() {
        super.stop();
    }

    /**
     * Starts the plugin's lifecycle by invoking the necessary initialization logic.
     * This overrides the default start behavior in the parent class and ensures
     * that the plugin is correctly prepared for usage.
     *
     * Subclasses can extend this method to include additional start-up functionality
     * specific to the plugin's requirements.
     */
    @Override
    public void start() {
        super.start();
    }

    /**
     * Deletes the plugin, performing any necessary cleanup tasks.
     * This method is invoked to remove the plugin from the system and release
     * any resources associated with it.
     *
     * Overrides the default delete behavior in the parent class.
     */
    @Override
    public void delete() {
        super.delete();
    }
}
