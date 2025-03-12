package com.example.track_flow.controller;

import com.example.track_flow.dto.PackageRequestDTO;
import com.example.track_flow.dto.PackageResponseDTO;
import com.example.track_flow.model.Package;
import com.example.track_flow.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packages")
@CrossOrigin
public class PackageController {

    @Autowired
    private PackageService packageService;

    @PostMapping
    public ResponseEntity<PackageResponseDTO> createPackage(@RequestBody PackageRequestDTO packageRequestDTO) {
        PackageResponseDTO createdPackage = packageService.createPackage(packageRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPackage);
    }

    @GetMapping
    public ResponseEntity<List<Package>> getAllPackages() {
        List<Package> packages = packageService.getAllPackages();
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/events")
    public ResponseEntity<List<String>> getAllEvents() {
        List<String> events = packageService.getAllEvents();
        return ResponseEntity.ok(events);
    }
}