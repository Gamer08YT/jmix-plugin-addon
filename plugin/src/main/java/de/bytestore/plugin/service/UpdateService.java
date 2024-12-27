package de.bytestore.plugin.service;

import de.bytestore.plugin.configuration.JMIXUpdateManager;
import de.bytestore.plugin.configuration.SpringRuntimePluginManager;
import de.bytestore.plugin.entity.Plugin;
import de.bytestore.plugin.entity.Repository;
import io.jmix.core.UnconstrainedDataManager;
import org.pf4j.update.DefaultUpdateRepository;
import org.pf4j.update.PluginInfo;
import org.pf4j.update.UpdateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
@Service
public class UpdateService {
    private static final Logger log = LoggerFactory.getLogger(UpdateService.class);

    @Autowired
    private UnconstrainedDataManager unconstrainedDataManager;

    @Autowired(required = false)
    private BuildProperties buildProperties;

    @Autowired
    private JMIXUpdateManager updateManager;

    @Autowired
    private SpringRuntimePluginManager pluginManager;

    @Autowired
    private Environment environment;


    @EventListener
    public void onApplicationStarted(final ApplicationStartedEvent event) {
        this.reloadRepositories();
    }

    /**
     * Retrieves the version of the application as specified in the build properties.
     *
     * @return the version string from the build properties
     */
    public String getVersion() {
        if (buildProperties == null) {
            log.error("No build properties found, ensure that build-info.properties was created during the build.");

            return "0.0.0";
        }

        return buildProperties.getVersion();
    }

    /**
     * Updates all outdated plugins by checking the list of available updates and executing the update process
     * for each outdated plugin found.
     *
     * @return a boolean value indicating whether the update process encountered any errors.
     *         Returns true if all updates were successfully applied, otherwise false if at least one update failed.
     */
    public boolean updateAll() {
        boolean stateIO = true;

        List<PluginInfo> updates = getOutdatedPlugins();

        log.debug("Found {} updates", updates.size());

        for (PluginInfo plugin : updates) {
            log.debug("Found update for plugin '{}'", plugin.id);

            // Update Plugin.
            if (this.updateRaw(plugin.id)) {
                stateIO = false;
            }
        }

        return stateIO;
    }

    /**
     * Updates a plugin identified by its ID to the latest available version.
     *
     * @param idIO the unique identifier of the plugin to be updated
     * @return {@code true} if the plugin was successfully updated to the latest version, {@code false} otherwise
     */
    private boolean updateRaw(String idIO) {
        PluginInfo.PluginRelease lastRelease = updateManager.getLastPluginRelease(idIO);
        String lastVersion = lastRelease.version;
        String installedVersion = pluginManager.getPlugin(idIO).getDescriptor().getVersion();

        log.debug("Update plugin '{}' from version {} to version {}", idIO, installedVersion, lastVersion);

        boolean updated = updateManager.updatePlugin(idIO, lastVersion);

        if (updated) {
            log.debug("Updated plugin '{}'", idIO);

            return true;
        } else {
            log.error("Cannot update plugin '{}'", idIO);

            return false;
        }
    }

    /**
     * Updates the plugin data using the provided plugin object.
     *
     * @param pluginIO the plugin object containing updated information
     * @return true if the update operation is successful, false otherwise
     */
    public boolean update(Plugin pluginIO) {
        return this.updateRaw(pluginIO.getId());
    }

    /**
     * Checks if an update is available for the specified plugin.
     *
     * @param pluginIO the plugin for which the availability of an update is to be checked
     * @return true if an update is available for the plugin, false otherwise
     */
    public boolean isUpdateAvailable(Plugin pluginIO) {
        return (this.getUpdateInfo(pluginIO) != null);
    }

    /**
     * Checks if the given plugin has an available update.
     *
     * @param pluginIO the plugin to check for updates
     * @return the PluginInfo object representing the update if available, or null if no update is found
     */
    public PluginInfo getUpdateInfo(Plugin pluginIO) {
        List<PluginInfo> updatesIO = getOutdatedPlugins();

        for (PluginInfo updateIO : updatesIO) {
            if (updateIO.id == pluginIO.getId()) {
                return updateIO;
            }
        }

        return null;
    }

    /**
     * Retrieves a list of plugins that have updates available.
     *
     * @return a list of PluginInfo objects representing the plugins with available updates
     */
    public List<PluginInfo> getOutdatedPlugins() {
        return updateManager.getUpdates();
    }

    /**
     * Determines whether the version check is enabled by retrieving the value
     * of the "version.check" property from the environment.
     * If the property is not set, the default value is true.
     *
     * @return true if the version check is enabled; false otherwise
     */
    public boolean isVersionCheck() {
        return environment.getProperty("plugins.version.check", Boolean.class, true);
    }

    /**
     * Determines whether the exact version of plugins is allowed by retrieving the
     * value of the "plugins.version.exact" property from the environment.
     * If the property is not set, the default value is false.
     *
     * @return true if the exact version is allowed; false otherwise
     */
    public boolean isExactVersionAllowed() {
        return environment.getProperty("plugins.version.exact", Boolean.class, false);
    }


    /**
     * Retrieves the latest release information of the specified plugin.
     *
     * @param plugin the plugin for which the latest release information is to be retrieved
     * @return the last release of the specified plugin, or null if no release is found
     */
    public PluginInfo.PluginRelease getLastRelease(Plugin plugin) {
        return updateManager.getLastPluginRelease(plugin.getId());
    }

    /**
     * Retrieves a list of repositories from the update manager, supplemented by additional repositories
     * loaded from the unconstrained data manager. Each additional repository is included in the list
     * if it is marked as enabled. If the repository URI is invalid, a runtime exception is thrown.
     *
     * @return a list of {@link UpdateRepository} objects, representing the combined repositories from
     *         the update manager and the enabled repositories from the data manager.
     */
    public List<UpdateRepository> getRepositories() {
        List<UpdateRepository> repositoriesIO = new ArrayList<>();

        unconstrainedDataManager.load(Repository.class).all().list().forEach(repositoryIO -> {
            try {
                // Add Repository to List if enabled.
                if (repositoryIO.getEnabled())
                    repositoriesIO.add(new DefaultUpdateRepository(repositoryIO.getId().toString(), new URL(repositoryIO.getUri())));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });

        return repositoriesIO;
    }

    /**
     * Reloads the repositories by fetching the latest repository data and updating
     * the repository manager. The method retrieves the updated list of repositories
     * and sets them in the update manager.
     */
    public void reloadRepositories() {
        updateManager.setRepositories(getRepositories());

        log.info("Reloaded {} repositories.", updateManager.getRepositories().size());
    }
}
