package de.bytestore.plugin.configuration;

import org.pf4j.PluginManager;
import org.pf4j.update.UpdateManager;
import org.pf4j.update.UpdateRepository;

import java.nio.file.Path;
import java.util.List;


/**
 * JMIXUpdateManager is a subclass of the UpdateManager class from the PF4J framework
 * that provides functionality for managing plugin updates. It is intended for use
 * within a plugin-enabled application and works in conjunction with a PluginManager instance
 * to handle updates from specified repositories or a repositories.json file.
 *
 * This class offers constructors with different parameter sets, allowing flexibility
 * in defining how repositories are configured. Plugin updates can be managed either
 * by providing a list of repositories or by specifying a path to a repositories.json configuration.
 *
 * Constructor options include:
 * - Using a PluginManager only: In this case, repositories may be initialized later.
 * - Specifying a repositories.json file path: Used to initialize repositories from a configuration file.
 * - Providing a list of UpdateRepository objects for direct repository setup.
 */
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
