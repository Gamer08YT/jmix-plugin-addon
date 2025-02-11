package de.bytestore.plugin.view.repository;

import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Repository;
import io.jmix.flowui.view.*;


/**
 * Represents a list view for managing Repository entities within the application.
 *
 * This class integrates with the Jmix framework and provides a standardized implementation
 * for displaying and interacting with a collection of Repository entities. It utilizes
 * the StandardListView functionality, which includes features like data binding, filtering,
 * and navigation to detailed views.
 *
 * Annotations:
 * - @Route: Specifies the URL route as "repositories" and associates the view with a layout class.
 * - @ViewController: Assigns a unique ID to the view for identification and integration purposes.
 * - @ViewDescriptor: Defines the associated XML descriptor used to configure the view's UI components.
 * - @LookupComponent: Identifies "repositoriesDataGrid" as the primary lookup component within the view.
 * - @DialogMode: Configures the dialog window mode with a specified width (e.g., "64em").
 *
 * This view is designed to work with Repository entities that store information about
 * plugin management repositories, their URIs, enabled status, and metadata like creation details.
 */
@Route(value = "repositories", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_Repository.list")
@ViewDescriptor(path = "repository-list-view.xml")
@LookupComponent("repositoriesDataGrid")
@DialogMode(width = "64em")
public class RepositoryListView extends StandardListView<Repository> {
}