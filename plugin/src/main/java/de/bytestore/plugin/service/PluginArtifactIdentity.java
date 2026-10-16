package de.bytestore.plugin.service;

/** Identifies the exact plugin archive whose installation terms the user accepted. */
public record PluginArtifactIdentity(String pluginId, String version, String sha256) {
}
