package de.bytestore.plugin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class UpdateService {
    private static final Logger log = LoggerFactory.getLogger(UpdateService.class);
    @Autowired(required = false)
    private BuildProperties buildProperties;

    @Autowired
    private Environment environment;

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


}
