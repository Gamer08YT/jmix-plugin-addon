package de.bytestore.plugin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import de.bytestore.plugin.bean.PluginView;
import io.jmix.core.impl.scanning.AnnotationScanMetadataReaderFactory;
import io.jmix.flowui.sys.ViewControllerMeta;
import io.jmix.flowui.sys.ViewDescriptorUtils;
import io.jmix.flowui.view.View;
import io.jmix.flowui.view.ViewInfo;
import io.jmix.flowui.view.ViewRegistry;
import io.jmix.flowui.view.template.impl.ViewTemplateDescriptorRegistry;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.LinkedHashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

/**
 * Represents a Jmix plugin built on top of the SpringPlugin implementation.
 * This class is responsible for extending the functionality of the
 * PF4J (Plugin Framework for Java) plugin system by adding support
 * for creating a Spring-based application context.
 *
 * The JmixPlugin class integrates with the Jmix framework, enabling
 * seamless interaction with other Jmix modules and configurations.
 *
 * Responsibilities:
 * - Initializes the plugin with a provided PluginWrapper instance.
 * - Overrides the method to create a Spring application context for the plugin.
 *
 * Note: The actual application context creation logic should be implemented
 * in place of returning a null value in the overridden method.
 */
public class JmixPlugin extends SpringPlugin {
    private static final Logger log = LoggerFactory.getLogger(JmixPlugin.class);
    // Store Annotation Config Context.
    private final AnnotationConfigApplicationContext contextIO = new AnnotationConfigApplicationContext();
    private final Set<Class<? extends Component>> registeredRoutes = new LinkedHashSet<>();
    private final Map<Class<? extends View<?>>, ViewInfo> pluginViews =
            new IdentityHashMap<>();
    private final Map<Class<? extends View<?>>, String> pluginTemplatePaths = new IdentityHashMap<>();
    private final Set<String> registeredViewBeans = new LinkedHashSet<>();

    public JmixPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * Creates and returns the application context for the plugin. This allows
     * the plugin to establish its own Spring application context, enabling
     * dependency injection, bean management, and integration with the Jmix framework.
     * The method must be implemented to provide the actual application context
     * relevant to the plugin's functionality.
     *
     * @return the application context specific to the plugin, or null if the
     *         application context creation is not implemented.
     */
    @Override
    protected ApplicationContext createApplicationContext() {
        contextIO.setClassLoader(getWrapper().getPluginClassLoader());

        return contextIO;
    }

    /**
     * Registers a plugin view at an application-wide route. Use this from {@link #start()}
     * after the host application has initialized its Vaadin session.
     *
     * @param path route path, for example {@code "reports"}
     * @param viewClass plugin view component
     */
    public void registerRoute(String path, Class<? extends Component> viewClass) {
        RouteConfiguration.forApplicationScope().setRoute(path, viewClass);
        registeredRoutes.add(viewClass);
    }

    /**
     * Registers a plugin view under the supplied router layout hierarchy.
     *
     * @param path route path, for example {@code "reports"}
     * @param viewClass plugin view component
     * @param layoutClasses parent layouts for the view
     */
    @SafeVarargs
    public final void registerRoute(String path, Class<? extends Component> viewClass,
                                    Class<? extends RouterLayout>... layoutClasses) {
        RouteConfiguration.forApplicationScope().setRoute(path, viewClass, layoutClasses);
        registeredRoutes.add(viewClass);
    }

    /** Removes a route registered by this plugin. */
    public void unregisterRoute(Class<? extends Component> viewClass) {
        RouteConfiguration.forApplicationScope().removeRoute(viewClass);
        registeredRoutes.remove(viewClass);
        @SuppressWarnings("unchecked")
        Class<? extends View<?>> jmixViewClass = (Class<? extends View<?>>) viewClass;
        ViewInfo viewInfo = pluginViews.remove(jmixViewClass);
        if (viewInfo != null) {
            ViewRegistry viewRegistry = AutowireLoader.getBean(ViewRegistry.class);
            viewRegistry.getRouteConfiguration().removeRoute(viewClass);
            removeViewInfo(viewRegistry, viewInfo);
            removePluginTemplate(jmixViewClass);
            viewRegistry.reset();
            removeViewBean(viewClass.getName());
        }
    }

    /**
     * Registers the provided class into the application context and refreshes the context.
     * This method allows for dynamically adding new configurations or beans to the plugin's
     * Spring application context during runtime.
     *
     * @param classIO the class to be registered within the application context. It is expected
     *                to define configurations, beans, or other components necessary for
     *                the plugin's functionality.
     */
    public void register(Class classIO) {
        contextIO.register(classIO);
        contextIO.refresh();
    }

    /**
     * Scans the specified base package and refreshes the application context.
     * This method utilizes the `scan` functionality of the Spring
     * `AnnotationConfigApplicationContext` to load components, beans, or configurations
     * from the provided package into the application context.
     *
     * @param packageIO the base package to be scanned for components and configurations.
     */
    public void scan(String packageIO) {
        contextIO.scan(packageIO);
        contextIO.refresh();
    }

    /**
     * Stops the plugin's lifecycle by invoking the necessary cleanup logic.
     * This method overrides the default stop behavior in the parent class
     * and ensures that any resources held by the plugin are properly
     * released or closed.
     *
     * Subclasses can extend this method to include additional cleanup
     * procedures specific to the plugin's functionality.
     */
    @Override
    public void stop() {
        ViewRegistry viewRegistry = AutowireLoader.getBean(ViewRegistry.class);
        registeredRoutes.forEach(viewClass -> {
            RouteConfiguration.forApplicationScope().removeRoute(viewClass);
            viewRegistry.getRouteConfiguration().removeRoute(viewClass);
        });
        registeredRoutes.clear();
        pluginViews.values().forEach(viewInfo -> removeViewInfo(viewRegistry, viewInfo));
        pluginViews.clear();
        List.copyOf(pluginTemplatePaths.keySet()).forEach(this::removePluginTemplate);
        pluginTemplatePaths.clear();
        registeredViewBeans.forEach(this::removeViewBean);
        registeredViewBeans.clear();
        viewRegistry.reset();
        contextIO.close();
        super.stop();
    }

    /**
     * Starts the plugin's lifecycle by invoking the necessary initialization logic.
     * This overrides the default start behavior in the parent class and ensures
     * that the plugin is correctly prepared for usage.
     *
     * Subclasses can extend this method to include additional start-up functionality
     * specific to the plugin's requirements.
     */
    @Override
    public void start() {
        super.start();
        registerAnnotatedPluginViews();
    }

    private void registerAnnotatedPluginViews() {
        ClassLoader pluginClassLoader = getWrapper().getPluginClassLoader();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addClassLoaders(pluginClassLoader)
                .setUrls(ClasspathHelper.forClassLoader(pluginClassLoader))
                .setScanners(Scanners.TypesAnnotated));

        Set<Class<?>> viewClasses = reflections.getTypesAnnotatedWith(PluginView.class, true);
        log.info("Plugin {} declares {} plugin views", getWrapper().getPluginId(), viewClasses.size());
        for (Class<?> viewClass : viewClasses) {
            Route route = viewClass.getAnnotation(Route.class);
            if (route == null || !Component.class.isAssignableFrom(viewClass)) {
                log.warn("Skipping plugin view {}: it must extend Component and declare @Route", viewClass.getName());
                continue;
            }

            @SuppressWarnings("unchecked")
            Class<? extends Component> componentClass = (Class<? extends Component>) viewClass;
            if (!View.class.isAssignableFrom(viewClass)) {
                log.warn("Skipping plugin view {}: Jmix views must extend View", viewClass.getName());
                continue;
            }

            ViewRegistry viewRegistry = AutowireLoader.getBean(ViewRegistry.class);
            @SuppressWarnings("unchecked")
            Class<? extends View<?>> jmixViewClass = (Class<? extends View<?>>) viewClass;
            AnnotationScanMetadataReaderFactory metadataReaderFactory =
                    AutowireLoader.getBean(AnnotationScanMetadataReaderFactory.class);
            ViewControllerMeta controllerMeta = new ViewControllerMeta(metadataReaderFactory, jmixViewClass);
            String templatePath = registerPluginTemplate(jmixViewClass, pluginClassLoader);
            ViewInfo viewInfo = new ViewInfo(controllerMeta.getId(), controllerMeta.getControllerClass(),
                    jmixViewClass, templatePath);
            viewRegistry.getViewInfos();
            addViewInfo(viewRegistry, viewInfo);
            pluginViews.put(jmixViewClass, viewInfo);
            registerViewBean(componentClass);
            viewRegistry.registerRoute(jmixViewClass);
            registeredRoutes.add(componentClass);
        }
    }

    private String registerPluginTemplate(Class<? extends View<?>> viewClass, ClassLoader pluginClassLoader) {
        String sourcePath = ViewDescriptorUtils.resolveTemplatePath(viewClass);
        String classLoaderPath = sourcePath.startsWith("/") ? sourcePath.substring(1) : sourcePath;
        java.net.URL resource = pluginClassLoader.getResource(classLoaderPath);
        if (resource == null) {
            throw new IllegalStateException("Plugin view template not found: " + sourcePath
                    + " for " + viewClass.getName());
        }
        try (var input = resource.openStream()) {
            ViewTemplateDescriptorRegistry descriptorRegistry =
                    AutowireLoader.getBean(ViewTemplateDescriptorRegistry.class);
            String registeredPath = descriptorRegistry.createPath(viewClass.getName());
            descriptorRegistry.put(registeredPath, new String(input.readAllBytes(), StandardCharsets.UTF_8));
            pluginTemplatePaths.put(viewClass, registeredPath);
            return registeredPath;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read plugin view template " + sourcePath, e);
        }
    }

    private void removePluginTemplate(Class<? extends View<?>> viewClass) {
        String path = pluginTemplatePaths.remove(viewClass);
        if (path != null) {
            AutowireLoader.getBean(ViewTemplateDescriptorRegistry.class).remove(path);
        }
    }

    private void registerViewBean(Class<? extends Component> viewClass) {
        ApplicationContext hostContext = AutowireLoader.getApplicationContext();
        if (!(hostContext instanceof ConfigurableApplicationContext configurableContext)
                || !(configurableContext.getBeanFactory() instanceof BeanDefinitionRegistry registry)) {
            throw new IllegalStateException("Host application context cannot register plugin view beans");
        }
        String beanName = viewClass.getName();
        if (!registry.containsBeanDefinition(beanName)) {
            registry.registerBeanDefinition(beanName,
                    BeanDefinitionBuilder.genericBeanDefinition(viewClass).getBeanDefinition());
            registeredViewBeans.add(beanName);
        }
    }

    private void removeViewBean(String beanName) {
        ApplicationContext hostContext = AutowireLoader.getApplicationContext();
        if (hostContext instanceof ConfigurableApplicationContext configurableContext
                && configurableContext.getBeanFactory() instanceof BeanDefinitionRegistry registry
                && registry.containsBeanDefinition(beanName)) {
            registry.removeBeanDefinition(beanName);
        }
    }

    @SuppressWarnings("unchecked")
    private void addViewInfo(ViewRegistry viewRegistry, ViewInfo viewInfo) {
        try {
            java.lang.reflect.Field viewsField = ViewRegistry.class.getDeclaredField("views");
            viewsField.setAccessible(true);
            Map<String, ViewInfo> views = (Map<String, ViewInfo>) viewsField.get(viewRegistry);
            views.put(viewInfo.getId(), viewInfo);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to register plugin view metadata", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void removeViewInfo(ViewRegistry viewRegistry, ViewInfo viewInfo) {
        try {
            java.lang.reflect.Field viewsField = ViewRegistry.class.getDeclaredField("views");
            viewsField.setAccessible(true);
            Map<String, ViewInfo> views = (Map<String, ViewInfo>) viewsField.get(viewRegistry);
            views.remove(viewInfo.getId(), viewInfo);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to unregister plugin view metadata", e);
        }
    }

    /**
     * Deletes the plugin, performing any necessary cleanup tasks.
     * This method is invoked to remove the plugin from the system and release
     * any resources associated with it.
     *
     * Overrides the default delete behavior in the parent class.
     */
    @Override
    public void delete() {
        super.delete();
    }
}
