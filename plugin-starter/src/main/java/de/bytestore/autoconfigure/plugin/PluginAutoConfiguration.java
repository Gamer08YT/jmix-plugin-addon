package de.bytestore.autoconfigure.plugin;

import de.bytestore.plugin.PluginConfiguration;
import io.jmix.data.DataConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({DataConfiguration.class, PluginConfiguration.class})
public class PluginAutoConfiguration {
}

