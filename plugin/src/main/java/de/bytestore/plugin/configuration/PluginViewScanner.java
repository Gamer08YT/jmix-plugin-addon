package de.bytestore.plugin.configuration;

import de.bytestore.plugin.bean.PluginView;
import io.jmix.flowui.view.ViewController;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Set;

/**
 * The PluginViewScanner class is a Spring configuration class that implements the
 * {@link BeanDefinitionRegistryPostProcessor} interface.
 * Its primary role is to dynamically scan and register plugin view classes,
 * which are annotated with {@link PluginView} and also marked as
 * {@link com.haulmont.cuba.gui.screen.ViewController}, as Spring beans in the application context.
 *
 * This functionality allows seamless integration of custom plugin views into the
 * Spring-managed environment without the need for explicit bean declarations,
 * enhancing modular*/
@Configuration
public class PluginViewScanner implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(PluginViewScanner.class);
    private ApplicationContext applicationContext;

    /**
     * Processes the Spring bean definition registry by scanning the classpath
     * for classes annotated with {@link PluginView}, ensuring they are also
     * annotated with {@link ViewController},
     * and registering them as beans in the Spring context.
     *
     * This method integrates custom plugin views into the Spring application context by
     * dynamically scanning and registering classes that are both plugin views
     * and JMIX ViewControllers as beans.
     *
     * @param registry the {@link BeanDefinitionRegistry} used to register
     *                 the dynamically discovered and annotated classes as beans
     * @throws BeansException if an error occurs during bean registration or while
     *                        interacting with the registry
     */
    @Override
    @Order(Ordered.LOWEST_PRECEDENCE)
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // Build Reflections scanner to scan the entire classpath
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(Scanners.TypesAnnotated)
        );

        // Find all classes annotated with @PluginView
        Set<Class<?>> pluginViewClasses = reflections.getTypesAnnotatedWith(PluginView.class, true);

        for (Class<?> clazz : pluginViewClasses) {

            // Ensure it's also a JMIX ViewController
            if (clazz.isAnnotationPresent(ViewController.class)) {
                String beanName = clazz.getName(); // or clazz.getSimpleName(), depending on your naming convention

                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                registry.registerBeanDefinition(beanName, builder.getBeanDefinition());


                log.info("Registered Plugin View: " + clazz.getName());
            } else
                log.warn("Class {} is annotated with @PluginView but is not a JMIX ViewController.", clazz.getName());
        }
    }

    /**
     * This method is meant to provide additional processing to the bean factory
     * in a Spring application context after its standard initialization but
     * before it is used by the application. It is part of the {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor}
     * lifecycle for custom processing or modification of the bean definitions.
     *
     * This implementation is not currently utilized and remains unimplemented.
     *
     * @param beanFactory the Spring {@link ConfigurableListableBeanFactory}
     *                    to be post-processed. This allows for custom modifications of the bean factory's
     *                    internal state or its metadata about existing bean definitions.
     */
    @Override
    @Order(Ordered.LOWEST_PRECEDENCE)
    public void postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory beanFactory) {
        // Nor implemented.
    }

    /**
     * Retrieves a list of all beans annotated with {@link PluginView} from the application context.
     * These beans represent plugin views that are integrated into the current Spring application context.
     *
     * @return a list of objects representing plugin views annotated with {@link PluginView}.
     */
    public List<Object> getPluginViews() {
        return applicationContext.getBeansWithAnnotation(PluginView.class).values().stream().toList();
    }

    /**
     * Sets the application context for the implementing class. This method is part
     * of the {@link ApplicationContextAware} interface and allows
     * the implementing bean to be aware of its owning {@link ApplicationContext}.
     *
     * @param applicationContext the {@link ApplicationContext} to be set, providing
     *                           access to the Spring application context and its
     *                           functionalities.
     * @throws BeansException if an error occurs while accessing the application context.
     */
    @Override
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}