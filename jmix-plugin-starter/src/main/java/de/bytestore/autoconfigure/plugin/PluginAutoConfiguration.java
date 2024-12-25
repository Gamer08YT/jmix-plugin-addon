package de.bytestore.autoconfigure.plugin;

import de.bytestore.plugin.PluginConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({PluginConfiguration.class})
public class PluginAutoConfiguration {
}

