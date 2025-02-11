package de.bytestore.plugin;

import io.jmix.core.annotation.JmixModule;
import io.jmix.core.impl.scanning.AnnotationScanMetadataReaderFactory;
import io.jmix.data.DataConfiguration;
import io.jmix.eclipselink.EclipselinkConfiguration;
import io.jmix.flowui.FlowuiConfiguration;
import io.jmix.flowui.sys.ActionsConfiguration;
import io.jmix.flowui.sys.ViewControllersConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Collections;

/**
 * PluginConfiguration class serves as a central configuration for the "de.bytestore.plugin" module.
 *
 * This configuration integrates with the Jmix framework's modular architecture and is responsible
 * for defining the following:
 *
 * - Component scanning for Spring-managed beans within the module.
 * - Automatic configuration properties scanning.
 * - Dependency declaration for other Jmix modules such as EclipselinkConfiguration and FlowuiConfiguration.
 * - Property source declaration for loading module-specific properties.
 *
 * The class provides bean definitions for:
 * - ViewControllersConfiguration: Configures view controllers for the module.
 * - ActionsConfiguration: Configures actions for UI elements in the module.
 */
@Configuration
@ComponentScan
@ConfigurationPropertiesScan
@JmixModule(dependsOn = {EclipselinkConfiguration.class, FlowuiConfiguration.class})
@PropertySource(name = "de.bytestore.plugin", value = "classpath:/de/bytestore/plugin/module.properties")
public class PluginConfiguration {

    @Bean("plugin_PluginViewControllers")
    public ViewControllersConfiguration screens(final ApplicationContext applicationContext,
                                                final AnnotationScanMetadataReaderFactory metadataReaderFactory) {
        final ViewControllersConfiguration viewControllers
                = new ViewControllersConfiguration(applicationContext, metadataReaderFactory);
        viewControllers.setBasePackages(Collections.singletonList("de.bytestore.plugin"));
        return viewControllers;
    }

    @Bean("plugin_PluginActions")
    public ActionsConfiguration actions(final ApplicationContext applicationContext,
                                        final AnnotationScanMetadataReaderFactory metadataReaderFactory) {
        final ActionsConfiguration actions
                = new ActionsConfiguration(applicationContext, metadataReaderFactory);
        actions.setBasePackages(Collections.singletonList("de.bytestore.plugin"));

        return actions;
    }
}
