package de.bytestore.plugin.listener;

import de.bytestore.plugin.entity.Repository;
import de.bytestore.plugin.service.UpdateService;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * RepositoryEventListener is responsible for handling events related to the {@link Repository} entity.
 * It listens to the saving event of a {@link Repository} and triggers updates to reload repository data.
 *
 * This class interacts with the {@link UpdateService} to perform necessary operations
 * upon the occurrence of repository-related events.
 */
@Component("plugin_RepositoryEventListener")
public class RepositoryEventListener {

    private final UpdateService updateService;

    public RepositoryEventListener(UpdateService updateService) {
        this.updateService = updateService;
    }

    /**
     * Handles the {@link EntitySavingEvent} for the {@link Repository} entity.
     * This method is triggered when a {@link Repository} is being saved and
     * reloads the repository data by invoking the {@link UpdateService#reloadRepositories()} method.
     *
     * @param event the {@link EntitySavingEvent} instance containing information
     *              about the {@link Repository} being saved
     */
    @EventListener
    public void onRepositorySaving(final EntitySavingEvent<Repository> event) {
        updateService.reloadRepositories();
    }


}