package de.bytestore.welcome.view.blank;


import com.vaadin.flow.router.Route;
import de.bytestore.plugin.bean.PluginView;
import io.jmix.flowui.view.DefaultMainViewParent;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@PluginView
@Route(value = "blank-view", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_BlankView")
@ViewDescriptor(path = "blank-view.xml")
public class BlankView extends StandardView {
}