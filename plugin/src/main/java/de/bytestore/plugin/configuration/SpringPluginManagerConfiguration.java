package de.bytestore.plugin.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.nio.file.Path;

/**
 * SpringPluginManagerConfiguration is a Spring configuration class that defines
 * the setup for plugin management within a Spring application context.
 *
 * This class configures the {@link SpringRuntimePluginManager} bean, which serves
 * as the plugin manager responsible for managing plugins at runtime. It is
 * initialized with the plugin's home directory, which can be specified through
 * application environment properties.
 *
 * The home directory for the plugins is resolved by reading the "plugins.home"
 * property from the environment. If the property is not defined, a default value
 * of "./plugins/" is used.
 */
@Configuration
public class SpringPluginManagerConfiguration {
    @Autowired
    private Environment environment;

    @Bean
    public SpringRuntimePluginManager pluginManager() {
        return new SpringRuntimePluginManager(Path.of(getHome()));
    }

    /**
     * Retrieves the home directory path for plugins from the environment properties.
     * The property key is "plugins.home". If not specified, the default value "./plugins/" is returned.
     *
     * @return the directory path for plugins as a String.
     */
    public String getHome() {
        return environment.getProperty("plugins.home", "./plugins/");
    }
}
