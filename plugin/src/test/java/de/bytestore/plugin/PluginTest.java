package de.bytestore.plugin;

import de.bytestore.plugin.entity.PluginData;
import io.jmix.core.UnconstrainedDataManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PluginTest {
	@Autowired
	private UnconstrainedDataManager dataManager;

	@Test
	void contextLoads() {
	}

	@Test
	void pluginDataValueCanBeLoadedFromHsqlDb() {
		PluginData data = dataManager.create(PluginData.class);
		data.setId("test.configuration");
		data.setValue("accepted-terms-sha256");
		dataManager.save(data);

		PluginData loaded = dataManager.load(PluginData.class)
				.id("test.configuration")
				.one();

		assertEquals("accepted-terms-sha256", loaded.getValue());
	}
}
