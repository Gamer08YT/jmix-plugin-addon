package de.bytestore.plugin.service;

import de.bytestore.plugin.entity.Plugin;
import io.jmix.core.DataManager;
import io.jmix.flowui.download.Downloader;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The PluginService class is responsible for managing the lifecycle, configuration, and operations
 * of plugins within the system using the SpringPluginManager. This includes actions such as loading,
 * enabling, disabling, starting, stopping plugins, as well as ensuring proper configuration and directory setup.
 *
 * The class leverages Spring's dependency injection and event handling to automate plugin operation
 * during application startup. It uses the application context and environment for configuration.
 *
 * Core operations include plugin loading, starting, stopping, and retrieving plugin states, as well as
 * directory management for plugin storage.
 */
@Component
@Service
public class PluginService {
    private static final Logger log = LoggerFactory.getLogger(PluginService.class);
    private final UpdateService updateService;
    private final ObjectProvider<Downloader> downloaderProvider;
    @Autowired
    protected DataManager dataManager;

    @Autowired
    private SpringPluginManager managerIO;

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationContext context;
    @Autowired
    private Downloader downloader;

    public PluginService(UpdateService updateService, ObjectProvider<Downloader> downloaderProvider) {
        this.updateService = updateService;
        this.downloaderProvider = downloaderProvider;
    }

    /**
     * Handles the {@link ApplicationStartedEvent} triggered when the application has started.
     * This method determines whether the plugin autoload feature is enabled by invoking {@code isAutoload()}.
     * If enabled, it invokes the {@code load()} method to load all plugins.
     *
     * @param event the application started event containing context about the application startup
     */
    @EventListener
    public void onApplicationStarted(final ApplicationStartedEvent event) {
        if (this.isAutoload())
            this.load();
    }


    /**
     * Loads all plugins managed by the SpringPluginManager.
     *
     * This method ensures the necessary plugin directory exists, sets the Spring application context
     * for the plugin manager, and proceeds to load the plugins from the specified home directory.
     * Logs the number of plugins loaded upon completion.
     */
    public void load() {
        this.checkDirectory();

        log.info("Loading plugins...");

        // Set Version if Version Check is enabled.
        if (updateService.isVersionCheck())
            managerIO.setSystemVersion(updateService.getVersion());

        // Enable or Disable Exact Version Mode. 1.1.0 === 1.1.0
        managerIO.setExactVersionAllowed(updateService.isExactVersionAllowed());

        // Set Spring Context for Plugin Manager.
        managerIO.setApplicationContext(context);

        // Load Plugins from Home.
        managerIO.loadPlugins();

        log.info("Loaded {} Plugins.", managerIO.getPlugins().size());
    }

    /**
     * Retrieves all loaded plugins managed by the SpringPluginManager.
     *
     * @return a collection of PluginWrapper objects representing the loaded plugins
     */
    public Collection<PluginWrapper> getPlugins() {
        return managerIO.getPlugins();
    }

    /**
     * Retrieves a specific plugin by its identifier.
     *
     * @param idIO the unique identifier of the plugin to be retrieved
     * @return the PluginWrapper object representing the plugin, or null if no plugin with the given identifier is found
     */
    public PluginWrapper getPlugin(String idIO) {
        return managerIO.getPlugin(idIO);
    }

    /**
     * Disables a plugin identified by its unique identifier.
     *
     * @param idIO the unique identifier of the plugin to be disabled
     * @return true if the plugin was successfully disabled, false otherwise
     */
    public boolean disablePlugin(String idIO) {
        return managerIO.disablePlugin(idIO);
    }

    /**
     * Enables a plugin identified by its unique identifier.
     *
     * @param idIO the unique identifier of the plugin to be enabled
     * @return true if the plugin was successfully enabled, false otherwise
     */
    public boolean enablePlugin(String idIO) {
        return managerIO.enablePlugin(idIO);
    }

    /**
     * Starts the plugin specified by its unique identifier.
     *
     * @param idIO the unique identifier of the plugin to be started
     * @return the state of the plugin after attempting to start it
     */
    public PluginState startPlugin(String idIO) {
        return managerIO.startPlugin(idIO);
    }

    /**
     * Stops the plugin with the specified ID.
     *
     * @param idIO the unique identifier of the plugin to stop
     * @return the state of the plugin after attempting to stop it
     */
    public PluginState stopPlugin(String idIO) {
        return managerIO.stopPlugin(idIO);
    }

    /**
     * Stops the operation of plugins managed by the I/O manager.
     * This method invokes the stop functionality for all plugins
     * handled by the manager, effectively halting their processes.
     */
    public void stop() {
        managerIO.stopPlugins();
    }

    /**
     * Starts the necessary plugins using the managerIO instance.
     * This method is responsible for initializing and running all
     * plugins that are configured in the system.
     */
    public void start() {
        managerIO.startPlugins();
    }

    /**
     * Ensures the existence of a directory specified by the `plugins.home` property.
     * If the directory does not exist, it will be created. Logs the creation of the directory
     * if it is newly created.
     */
    private void checkDirectory() {
        File fileIO = new File(getHome());

        if (!fileIO.exists()) {
            fileIO.mkdirs();

            log.info("Created plugins directory: {}", fileIO.getAbsolutePath());
        }
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

    /**
     * Determines whether the autoload feature for plugins is enabled.
     * The autoload configuration is retrieved from the environment properties using the key "plugins.autoload".
     * If the property is not specified, a default value of {@code true} is returned.
     *
     * @return a boolean indicating whether plugin autoloading is enabled.
     */
    public boolean isAutoload() {
        return environment.getProperty("plugins.autoload", Boolean.class, true);
    }

    /**
     * Converts a collection of PluginWrapper objects into a list of Plugin objects
     * by extracting relevant details and setting them in the Plugin objects.
     *
     * @return a list of Plugin objects created from the PluginWrapper collection
     *         associated with the current instance.
     */
    public List<Plugin> castPlugins() {
        List listIO = new ArrayList<>();

        for (PluginWrapper pluginWrapper : this.getPlugins()) {
            listIO.add(this.castPlugin(pluginWrapper));
        }

        listIO.add(getDummyPlugin("proxmox", "0.9.0"));
        listIO.add(getDummyPlugin("docker", "1.0.1"));
        listIO.add(getDummyPlugin("paypal", "2.0.0"));

        return listIO;
    }

    /**
     * Creates and returns a dummy plugin with preset values and the specified parameters.
     *
     * @param nameIO the identifier for the plugin being created
     * @param versionIO the required version for the plugin
     * @return a dummy Plugin instance with predefined properties
     */
    private Plugin getDummyPlugin(String nameIO, String versionIO) {
        Plugin pluginIO = dataManager.create(Plugin.class);

        pluginIO.setId(nameIO);
        pluginIO.setDescription("Dummy Plugin");
        pluginIO.setState(de.bytestore.plugin.entity.PluginState.STARTED);
        pluginIO.setVersion("1.0.0");
        pluginIO.setPath("./plugins/dummy.jar");
        pluginIO.setLicense("Apache 2.0");
        pluginIO.setProvider("ByteStore");
        pluginIO.setRequires(versionIO);

        return pluginIO;
    }

    /**
     * Converts a PluginWrapper object into a Plugin object by mapping its properties.
     *
     * @param pluginWrapper the PluginWrapper object to be cast into a Plugin object
     * @return a Plugin object containing the mapped properties from the PluginWrapper
     */
    public Plugin castPlugin(PluginWrapper pluginWrapper) {
        Plugin pluginIO = dataManager.create(Plugin.class);

        pluginIO.setId(pluginWrapper.getPluginId());
        pluginIO.setPath(pluginWrapper.getPluginPath().toString());
        pluginIO.setDescription(pluginWrapper.getDescriptor().getPluginDescription());
        pluginIO.setVersion(pluginWrapper.getDescriptor().getVersion());
        pluginIO.setLicense(pluginWrapper.getDescriptor().getLicense());
        pluginIO.setProvider(pluginWrapper.getDescriptor().getProvider());
        pluginIO.setRequires(pluginWrapper.getDescriptor().getRequires());
        pluginIO.setState(de.bytestore.plugin.entity.PluginState.valueOf(pluginWrapper.getPluginState().toString()));

        return pluginIO;
    }

    /**
     * Retrieves a plugin based on the supplied identifier and casts it to the Plugin type.
     *
     * @param id the identifier of the plugin, typically a string representing the plugin ID
     * @return the Plugin object casted from the retrieved plugin, or null if the id is not a string
     */
    public Plugin getPluginCasted(Object id) {
        if (id instanceof String) {
            return getDummyPlugin("proxmox", "1.0.0");
            //return castPlugin(getPlugin((String) id));
        }

        return null;
    }

    /**
     * Checks if the given plugin is outdated by comparing its version with the current version provided by the update service.
     *
     * @param pluginIO the plugin object whose version is to be checked
     * @return true if the plugin is outdated, false otherwise
     * @implNote https://github.com/pf4j/pf4j/blob/d763024aac175c2a5c3bedadc2986dd2111db65b/pf4j/src/main/java/org/pf4j/DependencyResolver.java#L142
     */
    public boolean isOutdated(Plugin pluginIO) {
        return !managerIO.getVersionManager().checkVersionConstraint(pluginIO.getRequires(), updateService.getVersion());
    }

    public void downloadPlugin(Plugin pluginIO) {
        File fileIO = new File(pluginIO.getPath());
        String nameIO = fileIO.getName();

        try {
            if (fileIO.exists()) {
                // Read File into Stream.
                FileInputStream inputStream = new FileInputStream(fileIO);

                // Upload File to Client.
                downloader.download(() -> inputStream, nameIO);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

