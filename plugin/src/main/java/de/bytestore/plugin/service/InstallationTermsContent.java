package de.bytestore.plugin.service;

import com.vaadin.flow.component.Component;

import java.util.List;

/** UI content produced by an optional installation-terms extension while its plugin JAR is staged. */
public record InstallationTermsContent(String title, List<Component> components) {
    public InstallationTermsContent {
        components = components == null ? List.of() : List.copyOf(components);
    }
}
