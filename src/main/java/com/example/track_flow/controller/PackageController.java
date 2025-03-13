package com.example.track_flow.controller;

import com.example.track_flow.dto.PackageRequestDTO;
import com.example.track_flow.dto.PackageResponseDTO;
import com.example.track_flow.model.Package;
import com.example.track_flow.service.PackageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packages")
@CrossOrigin
public class PackageController {
    private static final Logger logger = LoggerFactory.getLogger(PackageController.class);

    @Autowired
    private PackageService packageService;

    @PostMapping
    public ResponseEntity<PackageResponseDTO> createPackage(@RequestBody PackageRequestDTO packageRequestDTO) {
        logger.info("Requisição para criar um novo pacote com dados: {}", packageRequestDTO);
        PackageResponseDTO createdPackage = packageService.createPackage(packageRequestDTO);
        logger.info("Pacote criado com sucesso: {}", createdPackage);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPackage);
    }

    @GetMapping
    public ResponseEntity<List<Package>> getAllPackages() {
        logger.info("Requisição para listar todos os pacotes");
        List<Package> packages = packageService.getAllPackages();
        logger.info("Pacotes listados com sucesso: {}", packages);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/events")
    public ResponseEntity<List<String>> getAllEvents() {
        logger.info("Requisição para listar todos os eventos");
        List<String> events = packageService.getAllEvents();
        logger.info("Eventos listados com sucesso: {}", events);
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PackageResponseDTO> updatePackageStatus(@PathVariable String id, @RequestBody String status) {
        try {
            logger.info("Requisição para atualizar o status do pacote com id: {}", id);
            PackageResponseDTO updatedPackage = packageService.updatePackageStatus(id, status);
            logger.info("Status do pacote atualizado com sucesso: {}", updatedPackage);
            return ResponseEntity.ok(updatedPackage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}