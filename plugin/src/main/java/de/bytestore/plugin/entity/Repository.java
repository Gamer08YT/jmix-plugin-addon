package de.bytestore.plugin.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents a repository entity used for plugin management.
 *
 * This entity stores information about a repository including its URI, a flag indicating
 * whether the repository is enabled, and metadata related to its creation. It supports
 * tracking of creation details such as the user who created the entity and the creation
 * timestamp.
 *
 * The repository can be uniquely identified by its ID, which is a UUID.
 */
@JmixEntity
@Table(name = "PLUGIN_REPOSITORY")
@Entity(name = "plugin_Repository")
public class Repository {
    @InstanceName
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "URI", nullable = false)
    @NotNull
    private String uri;

    @Column(name = "ENABLED")
    private Boolean enabled = true;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private OffsetDateTime createdDate;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}