package de.bytestore.plugin.configuration;

import de.bytestore.plugin.service.PluginService;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class SpringPluginManagerConfiguration {
    @Autowired
    private PluginService pluginService;

    @Bean
    public SpringPluginManager pluginManager() {
        return new SpringPluginManager(Path.of(pluginService.getHome()));
    }
}
