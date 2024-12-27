package de.bytestore.plugin.configuration;

import org.pf4j.PluginManager;
import org.pf4j.update.UpdateManager;
import org.pf4j.update.UpdateRepository;

import java.nio.file.Path;
import java.util.List;


public class JMIXUpdateManager extends UpdateManager {

    public JMIXUpdateManager(PluginManager pluginManager) {
        super(pluginManager);
    }

    public JMIXUpdateManager(PluginManager pluginManager, Path repositoriesJson) {
        super(pluginManager, repositoriesJson);
    }

    public JMIXUpdateManager(PluginManager pluginManager, List<UpdateRepository> repos) {
        super(pluginManager, repos);
    }
}
