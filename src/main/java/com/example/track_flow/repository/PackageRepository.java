package com.example.track_flow.repository;

import com.example.track_flow.controller.PackageController;
import com.example.track_flow.model.Package;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<Package, UUID> {
    static final Logger logger = LoggerFactory.getLogger(PackageController.class);

    default Optional<Package> findByIdWithLog(UUID id) {
        logger.info("Buscando pacote com id: {}", id);
        return findById(id);
    }
}