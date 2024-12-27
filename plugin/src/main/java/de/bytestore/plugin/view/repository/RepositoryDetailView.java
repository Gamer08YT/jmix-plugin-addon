package de.bytestore.plugin.view.repository;

import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Repository;
import io.jmix.flowui.view.*;

@Route(value = "repositories/:id", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_Repository.detail")
@ViewDescriptor(path = "repository-detail-view.xml")
@EditedEntityContainer("repositoryDc")
public class RepositoryDetailView extends StandardDetailView<Repository> {
}