package de.bytestore.plugin.service;

import de.bytestore.plugin.entity.PluginData;
import io.jmix.core.UnconstrainedDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ConfigService provides functionality for managing key-value pair configuration
 * data for plugins. It utilizes the PluginData entity to store and retrieve
 * configuration settings. This service allows setting values, retrieving values
 * with default fallbacks, and handling the creation of new configuration entries
 * if they do not already exist.
 */
@Component
@Service
public class ConfigService {
    @Autowired
    private UnconstrainedDataManager unconstrainedDataManager;

    /**
     * Sets the value associated with a given key in the plugin data. If the plugin data
     * associated with the key does not exist, it is created. The provided value is
     * converted to a string before being saved.
     *
     * @param keyIO   the identifier for the plugin data to retrieve or create
     * @param valueIO the value to associate with the specified key
     */
    public void setValue(String keyIO, Object valueIO) {
        PluginData dataIO = getValueOrCreate(keyIO);

        dataIO.setValue(valueIO.toString());

        unconstrainedDataManager.save(dataIO);
    }

    /**
     * Retrieves or creates a {@link PluginData} object based on the given identifier.
     * If a PluginData object with the specified identifier exists, it is returned;
     * otherwise, a new PluginData object is created, initialized with the given identifier,
     * and returned.
     *
     * @param keyIO the identifier for the {@link PluginData} to retrieve or create
     * @return the existing or newly created {@link PluginData} object
     */
    private PluginData getValueOrCreate(String keyIO) {
        Optional<PluginData> dataIO = getObject(keyIO);

        if (dataIO.isEmpty()) {
            PluginData newIO = unconstrainedDataManager.create(PluginData.class);

            newIO.setId(keyIO);

            return newIO;
        }

        return dataIO.get();
    }

    /**
     * Retrieves the value associated with the specified key. If the key does not exist,
     * returns the provided default value.
     *
     * @param keyIO      the identifier for the plugin data to retrieve
     * @param defaultIO  the value to return if the specified key does not exist
     * @return the value associated with the specified key, or the default value if the key does not exist
     */
    public Object getValue(String keyIO, Object defaultIO) {
        Optional<PluginData> dataIO = getObject(keyIO);

        if (dataIO.isEmpty()) {
            return defaultIO;
        }

        return dataIO.get().getValue();
    }

    /**
     * Retrieves an optional PluginData object based on the given identifier.
     *
     * @param keyIO the identifier of the PluginData to be loaded
     * @return an Optional containing the PluginData if it exists, else empty
     */
    private Optional<PluginData> getObject(String keyIO) {
        return unconstrainedDataManager.load(PluginData.class).id(keyIO).optional();
    }
}
