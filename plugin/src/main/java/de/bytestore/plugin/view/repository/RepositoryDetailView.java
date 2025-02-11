package de.bytestore.plugin.view.repository;

import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Repository;
import io.jmix.flowui.view.*;

/**
 * Represents a detailed view for a single Repository entity.
 *
 * This view is used to manage and edit the details of a Repository entity. It is integrated
 * with the Jmix framework to provide a standard detail view functionality that includes
 * entity loading, form fields binding, and data persistence mechanisms.
 *
 * Features:
 * - Displays the information of a Repository entity.
 * - Allows editing and saving changes to the Repository entity.
 * - Binds to the "repositoryDc" data container, which manages the state of the entity being edited.
 * - Corresponds to the "repository-detail-view.xml" descriptor for layout and UI configuration.
 *
 * Route information:
 * - It is mapped to the route "repositories/:id", making it accessible via this URL path.
 * - Uses the DefaultMainViewParent layout as its parent component.
 *
 * Related components:
 * - List view: {@link RepositoryListView}, which displays a list of repositories and provides navigation to this detail view.
 * - Entity: {@link Repository}, which represents the Repository entity managed by this view.
 */
@Route(value = "repositories/:id", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_Repository.detail")
@ViewDescriptor(path = "repository-detail-view.xml")
@EditedEntityContainer("repositoryDc")
public class RepositoryDetailView extends StandardDetailView<Repository> {
}