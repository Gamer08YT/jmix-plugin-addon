package de.bytestore.plugin.view.repository;

import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Repository;
import io.jmix.flowui.view.*;


@Route(value = "repositories", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_Repository.list")
@ViewDescriptor(path = "repository-list-view.xml")
@LookupComponent("repositoriesDataGrid")
@DialogMode(width = "64em")
public class RepositoryListView extends StandardListView<Repository> {
}