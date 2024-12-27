package de.bytestore.plugin.view.repository;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.bytestore.plugin.entity.Repository;
import io.jmix.core.validation.group.UiCrossFieldChecks;
import io.jmix.flowui.action.SecuredBaseAction;
import io.jmix.flowui.component.UiComponentUtils;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.validation.ValidationErrors;
import io.jmix.flowui.kit.action.Action;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.model.InstanceLoader;
import io.jmix.flowui.view.*;

@Route(value = "repositories", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_Repository.list")
@ViewDescriptor(path = "repository-list-view.xml")
@LookupComponent("repositoriesDataGrid")
@DialogMode(width = "64em")
public class RepositoryListView extends StandardListView<Repository> {

    @ViewComponent
    private DataContext dataContext;

    @ViewComponent
    private CollectionContainer<Repository> repositoriesDc;

    @ViewComponent
    private InstanceContainer<Repository> repositoryDc;

    @ViewComponent
    private InstanceLoader<Repository> repositoryDl;

    @ViewComponent
    private VerticalLayout listLayout;

    @ViewComponent
    private DataGrid<Repository> repositoriesDataGrid;

    @ViewComponent
    private FormLayout form;

    @ViewComponent
    private HorizontalLayout detailActions;

    @Subscribe
    public void onInit(final InitEvent event) {
        repositoriesDataGrid.getActions().forEach(action -> {
            if (action instanceof SecuredBaseAction secured) {
                secured.addEnabledRule(() -> listLayout.isEnabled());
            }
        });
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        updateControls(false);
    }

    @Subscribe("repositoriesDataGrid.create")
    public void onRepositoriesDataGridCreate(final ActionPerformedEvent event) {
        dataContext.clear();
        Repository entity = dataContext.create(Repository.class);
        repositoryDc.setItem(entity);
        updateControls(true);
    }

    @Subscribe("repositoriesDataGrid.edit")
    public void onRepositoriesDataGridEdit(final ActionPerformedEvent event) {
        updateControls(true);
    }

    @Subscribe("saveButton")
    public void onSaveButtonClick(final ClickEvent<JmixButton> event) {
        Repository item = repositoryDc.getItem();
        ValidationErrors validationErrors = validateView(item);
        if (!validationErrors.isEmpty()) {
            ViewValidation viewValidation = getViewValidation();
            viewValidation.showValidationErrors(validationErrors);
            viewValidation.focusProblemComponent(validationErrors);
            return;
        }
        dataContext.save();
        repositoriesDc.replaceItem(item);
        updateControls(false);
    }

    @Subscribe("cancelButton")
    public void onCancelButtonClick(final ClickEvent<JmixButton> event) {
        dataContext.clear();
        repositoryDl.load();
        updateControls(false);
    }

    @Subscribe(id = "repositoriesDc", target = Target.DATA_CONTAINER)
    public void onRepositoriesDcItemChange(final InstanceContainer.ItemChangeEvent<Repository> event) {
        Repository entity = event.getItem();
        dataContext.clear();
        if (entity != null) {
            repositoryDl.setEntityId(entity.getId());
            repositoryDl.load();
        } else {
            repositoryDl.setEntityId(null);
            repositoryDc.setItem(null);
        }
        updateControls(false);
    }

    protected ValidationErrors validateView(Repository entity) {
        ViewValidation viewValidation = getViewValidation();
        ValidationErrors validationErrors = viewValidation.validateUiComponents(form);
        if (!validationErrors.isEmpty()) {
            return validationErrors;
        }
        validationErrors.addAll(viewValidation.validateBeanGroup(UiCrossFieldChecks.class, entity));
        return validationErrors;
    }

    private void updateControls(boolean editing) {
        UiComponentUtils.getComponents(form).forEach(component -> {
            if (component instanceof HasValueAndElement<?, ?> field) {
                field.setReadOnly(!editing);
            }
        });

        detailActions.setVisible(editing);
        listLayout.setEnabled(!editing);
        repositoriesDataGrid.getActions().forEach(Action::refreshState);
    }

    private ViewValidation getViewValidation() {
        return getApplicationContext().getBean(ViewValidation.class);
    }
}