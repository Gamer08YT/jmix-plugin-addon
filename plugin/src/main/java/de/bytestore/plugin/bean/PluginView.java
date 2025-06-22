package de.bytestore.plugin.bean;

import java.lang.annotation.*;

/**
 * Annotation to mark a class as a plugin view.
 * This annotation is intended to be used for identifying and handling
 * components or classes that represent views within a plugin system.
 *
 * It is annotated with Target(ElementType.TYPE), meaning it can only
 * be applied to class, interface, or enum declarations. The Retention
 * policy is set to RUNTIME, so it can be accessed via reflection at runtime.
 *
 * This annotation serves as metadata and does not contain any methods.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PluginView {
}