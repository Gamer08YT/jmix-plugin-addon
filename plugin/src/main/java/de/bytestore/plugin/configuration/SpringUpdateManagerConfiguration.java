package de.bytestore.plugin.configuration;

import org.pf4j.update.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * SpringUpdateManagerConfiguration is a Spring component that configures a JMIXUpdateManager
 * bean for managing plugin updates in a Spring application. The configuration utilizes a
 * SpringRuntimePluginManager instance as the plugin manager for the JMIXUpdateManager.
 *
 * This class is responsible for creating and injecting an instance of JMIXUpdateManager
 * into the application context. The update manager is initialized*/
@Component
public class SpringUpdateManagerConfiguration {
    @Autowired
    private SpringRuntimePluginManager managerIO;

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public JMIXUpdateManager updateManager() {
        return new JMIXUpdateManager(managerIO, new ArrayList<UpdateRepository>());
    }
}
