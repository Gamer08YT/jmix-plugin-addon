package de.bytestore.plugin.configuration;

import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.nio.file.Path;

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
