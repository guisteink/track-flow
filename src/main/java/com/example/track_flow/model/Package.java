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

/**
 * Documentação dos índices para a tabela "pacotes":
 *
 * 1. idx_sender: Índice no campo "sender" melhora o desempenho de consultas que filtram pacotes pelo remetente.
 * 2. idx_recipient: Índice no campo "recipient" acelera as buscas ao pesquisar pacotes pelo destinatário.
 * 3. idx_status: Índice no campo "status" acelera consultas filtrando pelo status de entrega, crucial para rastrear o progresso do pacote.
 * 4. idx_created_at: Índice no campo "createdAt" otimiza consultas que ordenam ou filtram pacotes pela data de criação,
 *    importante para relatórios e auditorias.
 *
 * Estes índices são empregados para reduzir o tempo de resposta das consultas, evitar varreduras completas na tabela,
 * e assegurar uma recuperação eficiente dos dados conforme o volume aumenta.
 */
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

    /**
     * Relação de um para muitos entre Package e Event.
     * 
     * Este atributo representa a lista de eventos associados a um Package.
     * A anotação @OneToMany define que um Package pode possuir múltiplos eventos,
     * enquanto o mappedBy indica que a propriedade "pkg" na entidade Event é responsável
     * por estabelecer a relação. O CascadeType.ALL garante que todas as operações
     * (como persistência, remoção, etc.) executadas em um Package sejam automaticamente
     * propagadas aos seus eventos. O uso do FetchType.LAZY aprimora a performance,
     * carregando os eventos somente quando necessário.
     */
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