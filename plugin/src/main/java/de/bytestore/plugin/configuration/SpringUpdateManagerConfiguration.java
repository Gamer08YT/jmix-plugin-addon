package de.bytestore.plugin.configuration;

import de.bytestore.plugin.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SpringUpdateManagerConfiguration {
    @Autowired
    private SpringRuntimePluginManager managerIO;

    @Autowired
    private UpdateService updateService;

    @Bean
    public JMIXUpdateManager updateManager() {
        JMIXUpdateManager updaterIO = new JMIXUpdateManager(managerIO);

        // Set Repositorys of UpdateManager.
        updaterIO.setRepositories(updateService.getRepositories());

        return updaterIO;
    }
}
