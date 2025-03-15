package com.example.track_flow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="eventos")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;

    @NotBlank(message = "Description is mandatory")
    @Column(nullable = false)
    private String description;

    @NotNull
    @Column(updatable = false)
    private LocalDateTime timestamp;

    @NotBlank(message = "Localization is mandatory")
    @Column(nullable = false)
    private String localization;

    @JsonIgnoreProperties("events")
    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private Package pkg;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public Package getPkg() {
        return pkg;
    }

    public void setPkg(Package pkg) {
        this.pkg = pkg;
    }
}