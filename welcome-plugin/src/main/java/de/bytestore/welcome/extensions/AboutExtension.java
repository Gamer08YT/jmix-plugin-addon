package de.bytestore.welcome.extensions;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import de.bytestore.plugin.extension.PluginAboutExtensionPoint;
import org.pf4j.Extension;

import java.util.List;

/** Example About tab built from Flow UI components. */
@Extension
public class AboutExtension implements PluginAboutExtensionPoint {
    @Override
    public List<Component> render() {
        return List.of(
                new H3("Welcome Plugin"),
                new Paragraph("This tab can contain Jmix Flow UI components supplied by the plugin."));
    }
}
