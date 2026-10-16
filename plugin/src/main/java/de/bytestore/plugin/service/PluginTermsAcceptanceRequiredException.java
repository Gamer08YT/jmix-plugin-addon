package de.bytestore.plugin.service;

/** Raised when installation terms have not been accepted for the plugin's current archive. */
public class PluginTermsAcceptanceRequiredException extends RuntimeException {
    public PluginTermsAcceptanceRequiredException(String pluginId) {
        super("Installation terms have not been accepted for plugin " + pluginId);
    }
}
