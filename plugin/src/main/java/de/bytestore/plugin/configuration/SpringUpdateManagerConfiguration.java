package de.bytestore.plugin.configuration;

import org.pf4j.update.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SpringUpdateManagerConfiguration {
    @Autowired
    private SpringRuntimePluginManager managerIO;

    @Bean
    public JMIXUpdateManager updateManager() {
        return new JMIXUpdateManager(managerIO, new ArrayList<UpdateRepository>());
    }
}
