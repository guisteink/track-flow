package com.example.track_flow.repository;

import com.example.track_flow.model.Package;
import com.example.track_flow.model.Package.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<Package, UUID> {

    @Query("SELECT p FROM Package p " +
        "WHERE (:sender IS NULL OR LOWER(p.sender) = LOWER(:sender)) " +
        "AND (:recipient IS NULL OR LOWER(p.recipient) = LOWER(:recipient)) " +
        "AND (:status IS NULL OR p.status = :status) " +
        "AND (:createdAt IS NULL OR p.createdAt >= :createdAt)")
    List<Package> findByFilters(@Param("sender") String sender,
                                @Param("recipient") String recipient,
                                @Param("status") Status status,
                                @Param("createdAt") LocalDateTime createdAt);
}