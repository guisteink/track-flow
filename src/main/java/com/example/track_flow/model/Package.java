package com.example.track_flow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "pacotes",
    indexes = {
        @Index(name = "idx_sender", columnList = "sender"),
        @Index(name = "idx_recipient", columnList = "recipient"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "createdAt")
    }
)
@Data
@NoArgsConstructor
public class Package {
    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @NotBlank(message = "Description is mandatory")
    @Column(nullable = false, length = 255)
    private String description;

    @NotBlank(message = "Sender is mandatory")
    @Column(nullable = false, length = 100)
    private String sender;

    @NotBlank(message = "Recipient is mandatory")
    @Column(nullable = false, length = 100)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.CREATED;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime estimatedDeliveryDate;

    @Column
    private LocalDateTime deliveredAt;

    @Column(length = 255)
    private String funFact;

    @Column
    private boolean isHoliday;

    @JsonIgnoreProperties("pkg")
    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events;

    public enum Status {
        CREATED, IN_TRANSIT, DELIVERED, CANCELED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}