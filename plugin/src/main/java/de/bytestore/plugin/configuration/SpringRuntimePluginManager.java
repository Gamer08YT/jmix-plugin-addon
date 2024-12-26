package de.bytestore.plugin.configuration;

import org.pf4j.RuntimeMode;
import org.pf4j.spring.SpringPluginManager;

import java.nio.file.Path;

/**
 * The SpringRuntimePluginManager class extends the SpringPluginManager to manage plugins
 * in a Spring application context. This implementation allows for setting up and configuring
 * plugin directories at runtime.
 */
public class SpringRuntimePluginManager extends SpringPluginManager {
    public static RuntimeMode runtimeMode = RuntimeMode.DEPLOYMENT;


    public SpringRuntimePluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }

    /**
     *
     * @return
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
