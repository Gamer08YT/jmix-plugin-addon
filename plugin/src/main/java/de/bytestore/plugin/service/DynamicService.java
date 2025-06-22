package de.bytestore.plugin.service;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.AmbiguousRouteConfigurationException;
import io.jmix.flowui.view.View;
import io.jmix.flowui.view.ViewRegistry;
import io.jmix.flowui.view.navigation.RouteSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ViewResolver;

/**
 * A service class responsible for dynamically managing views and their configurations.
 * This class utilizes a {@code ViewRegistry} for loading view components and handles
 * session-specific route configurations.
 */
@Component("plugin_DynamicService")
public class DynamicService {
    private static final Logger log = LoggerFactory.getLogger(DynamicService.class);


    @Autowired
    private ViewRegistry viewRegistry;


    /**
     * Loads a view dynamically based on the provided view class type.
     *
     * @param viewClass the class object representing the view to be loaded
     */
    public void loadView(Class<? extends View> viewClass) {
        this.loadView(viewClass.getName());
    }

    /**
     * Loads a view class dynamically based on the provided class name.
     *
     * @see https://github.com/jmix-framework/jmix/blob/f588b16742942cf1fa5b6ad5ebe93421617c8c0b/jmix-flowui/flowui/src/main/java/io/jmix/flowui/view/ViewRegistry.java#L555
     * @param className the fully qualified name of the view class to be loaded
     */
    public void loadView(String className) {
        viewRegistry.loadViewClass(className);
    }


    /**
     * Retrieves the route configuration for the current session scope.
     *
     * @return a {@code RouteConfiguration} object specific to the session scope
     */
    public RouteConfiguration getRouteConfiguration() {
        return RouteConfiguration.forSessionScope();
    }

}