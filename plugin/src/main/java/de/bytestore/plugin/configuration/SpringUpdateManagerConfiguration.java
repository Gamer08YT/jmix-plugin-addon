package de.bytestore.plugin.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SpringUpdateManagerConfiguration {
    @Autowired
    private SpringRuntimePluginManager managerIO;

    @Bean
    public JMIXUpdateManager updateManager() {
        return new JMIXUpdateManager(managerIO);
    }
}
