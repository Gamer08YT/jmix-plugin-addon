package de.bytestore.plugin.configuration;

import org.pf4j.RuntimeMode;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.nio.file.Path;

/**
 * The SpringRuntimePluginManager class extends the SpringPluginManager to manage plugins
 * in a Spring application context. This implementation allows for setting up and configuring
 * plugin directories at runtime.
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class SpringRuntimePluginManager extends SpringPluginManager {

    /**
     * A static variable representing the current runtime mode of the application or plugin manager.
     * This variable is used to indicate whether the application is running in deployment mode
     * or development mode. The runtime mode can influence the behavior of the system, such as
     * output verbosity, debugging capabilities, or dynamic class reloading.
     *
     * The default value is set to {@code RuntimeMode.DEPLOYMENT}, which typically corresponds
     * to a production environment.
     */
    public static RuntimeMode runtimeMode = RuntimeMode.DEPLOYMENT;


    /**
     * Constructs a new SpringRuntimePluginManager instance with the specified plugin root directories.
     * This manager is used for managing and loading plugins in a Spring application context.
     *
     * @param pluginsRoots an array of {@link Path} objects representing the directories
     *                     where plugins are located. These paths are used as the root directories
     *                     for managing plugins at runtime.
     */
    public SpringRuntimePluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }

    /**
     * Retrieves the current runtime mode of the plugin manager.
     *
     * @return the current runtime mode, which indicates whether the plugin manager
     *         is operating in development or deployment mode.
     */
    @Override
    public RuntimeMode getRuntimeMode() {
        return runtimeMode;
    }

    /**
     * Sets the runtime mode for the plugin manager.
     *
     * @param runtimeMode the runtime mode to be set. This can be used to specify the
     *                    behavior of the plugin manager, such as development or deployment mode.
     */
    public void setRuntimeMode(RuntimeMode runtimeMode) {
        SpringRuntimePluginManager.runtimeMode = runtimeMode;
    }
}
