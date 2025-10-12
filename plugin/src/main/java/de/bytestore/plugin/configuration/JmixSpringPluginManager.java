package de.bytestore.plugin.configuration;

import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;
import org.pf4j.spring.SpringExtensionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.nio.file.Path;
import java.util.List;

/**
 * JmixSpringPluginManager is a subclass of {@link DefaultPluginManager} and implements
 * the {@link ApplicationContextAware} interface to provide additional functionality
 * for managing plugins within a Spring-based application context.
 *
 * This class integrates the functionality of the PF4J plugin framework with the
 * Spring application context, allowing for seamless interaction between plugins
 * and Spring-managed beans. It makes use of a custom {@link SpringExtensionFactory} to
 * create plugin extensions compatible with Spring's dependency injection framework.
 *
 * Key features include:
 * - Extended constructors to initialize the plugin manager with specific plugin roots.
 * - Support for Spring's application context injection to enable integration of plugins
 *   with the application.
 * - Lazy initialization of plugins through the `init` method, ensuring compatibility
 *   with the startup order of the Jmix framework.
 *
 * Note: This implementation includes a workaround to address a known issue where
 * plugins load faster than the Jmix application initialization. As such, the `init`
 * method is provided but intentionally left with no operation.
 */
public class JmixSpringPluginManager extends DefaultPluginManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public JmixSpringPluginManager() {
        super();
    }

    public JmixSpringPluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }

    public JmixSpringPluginManager(List<Path> pluginsRoots) {
        super(pluginsRoots);
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new SpringExtensionFactory(this);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    /**
     * This method load, start plugins and inject extensions in Spring
     */
    public void init() {
        // Do nothing based on Autoload issue / Plugins are loading faster than JMIX starts.
        // Just a dirty workarround.
    }
}
