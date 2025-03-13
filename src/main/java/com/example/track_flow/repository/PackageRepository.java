package com.example.track_flow.repository;

import com.example.track_flow.model.Package;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<Package, UUID> {
}