package de.bytestore.welcome;

import de.bytestore.plugin.JmixPlugin;

public class WelcomePlugin extends JmixPlugin {

    public WelcomePlugin(org.pf4j.PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        log.info("WelcomePlugin.start()");

    }

    @Override
    public void stop() {
        log.info("WelcomePlugin.stop()");
    }
}