package de.bytestore.plugin;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * The AutowireLoader class provides a utility for accessing Spring-managed beans
 * through the ApplicationContext. It implements the ApplicationContextAware interface
 * to obtain and store the ApplicationContext reference during initialization.
 *
 * Responsibilities:
 * - Stores the reference to the Spring ApplicationContext during initialization.
 * - Provides a method to retrieve beans from the ApplicationContext by type.
 *
 * This class is designed as a Spring-managed component and should be used
 * to manage bean retrieval where direct application context access is required.
 */
@Component
public class AutowireLoader implements ApplicationContextAware {
    private static ApplicationContext context;

    /**
     * Sets the ApplicationContext that this object runs in.
     *
     * @param applicationContext the ApplicationContext object to be used by this class,
     *                           must not be null
     * @throws BeansException if context is not set properly
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * Retrieves a bean from the Spring Application Context based on its class type.
     *
     * @param <T> the type of the bean to retrieve
     * @param beanClass the class object of the bean to retrieve
     * @return the bean instance of the specified type
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    /**
     * Checks whether the Spring ApplicationContext is initialized and ready to use.
     *
     * @return true if the ApplicationContext has been set, false otherwise
     */
    public static boolean isReady() {
        return context != null;
    }
}