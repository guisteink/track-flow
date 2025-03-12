package com.example.track_flow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pacotes")
@Data
@NoArgsConstructor
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.CREATED;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column
    private LocalDateTime estimatedDeliveryDate;
    
    @Column
    private LocalDateTime deliveredAt;

    @Column
    private String funFact;

    @Column
    private boolean isHoliday;

    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events;

    public enum Status {
        CREATED,
        IN_TRANSIT,
        DELIVERED,
        CANCELED
    }
}