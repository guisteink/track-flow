package com.example.track_flow.controller;

import com.example.track_flow.dto.CancelResponseDTO;
import com.example.track_flow.dto.PackageRequestDTO;
import com.example.track_flow.dto.PackageResponseDTO;
import com.example.track_flow.dto.UpdatePackageStatusRequestDTO;
import com.example.track_flow.model.Event;
import com.example.track_flow.model.Package;
import com.example.track_flow.service.PackageService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/packages")
@CrossOrigin
public class PackageController {
    private static final Logger logger = LoggerFactory.getLogger(PackageController.class);

    @Autowired
    private PackageService packageService;

    @PostMapping
    public ResponseEntity<PackageResponseDTO> createPackage(@Valid @RequestBody PackageRequestDTO packageRequestDTO) {
        logger.info("Iniciando criação de pacote. Requisição recebida: {}", packageRequestDTO);
        PackageResponseDTO createdPackage = packageService.createPackage(packageRequestDTO);
        logger.info("Pacote criado com sucesso. ID: {} - Status: {}", createdPackage.getId(), createdPackage.getStatus());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(createdPackage.getId())
                        .toUri();
        return ResponseEntity
            .created(location)
            .header("Cache-Control", "no-store") 
            .body(createdPackage);
    }

    @GetMapping
    public ResponseEntity<List<Package>> getAllPackages(
    ) {
        logger.info("Solicitação para listar todos os pacotes iniciada.");
        List<Package> packages = packageService.getAllPackages();
        logger.info("Listagem de pacotes realizada com êxito. Total de pacotes retornados: {}", packages.size());
        return ResponseEntity.ok()
                .header("Cache-Control", "max-age=60, public") 
                .body(packages);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Package>> getPackagesByFilter(
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String createdAt
    ) {
        logger.info("Iniciando filtro de pacotes com sender: {}, recipient: {}, status: {} e createdAt: {}",
                    sender, recipient, status, createdAt);
        List<Package> filteredPackages = packageService.getPackagesByFilter(sender, recipient, status, createdAt);
        logger.info("Número de pacotes filtrados: {}", filteredPackages.size());
        return ResponseEntity.ok()
                .header("Cache-Control", "max-age=60, public") 
                .body(filteredPackages);
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<Event>> getPackageEvents(@PathVariable UUID id) {
        logger.info("Iniciando listagem de eventos para o pacote com id: {}", id);
        List<Event> events = packageService.getPackageEvents(id);
        String eTag = Integer.toHexString(events.hashCode()); 
        logger.info("Listagem de eventos concluída para o pacote com id: {}. Total de eventos: {}", id, events.size());
        return ResponseEntity.ok()
                .eTag(eTag) 
                .header("Cache-Control", "max-age=60, public") 
                .body(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackageResponseDTO> getPackageById(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "true") Boolean showEvents,
            @RequestHeader(value="If-None-Match", required=false) String ifNoneMatch
    ) {
        logger.info("Busca de pacote iniciada para o id: {} com parâmetro showEvents={}", id, showEvents);
        PackageResponseDTO pkg = packageService.getPackageById(id, showEvents);
        if (pkg == null) {
            logger.warn("Nenhum pacote encontrado para o id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        /* Verifica o cabeçalho If-None-Match" para validação de cache utilizando ETag */
        String eTag = Integer.toHexString(pkg.hashCode());
        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            logger.info("Recurso não modificado para o id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(eTag).build();
        }
        logger.info("Pacote encontrado para o id: {}. Detalhes: {}", id, pkg);
        return ResponseEntity.ok()
                .eTag(eTag)
                .header("Cache-Control", "max-age=60, public")
                .body(pkg);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PackageResponseDTO> updatePackageStatus(@PathVariable UUID id, @RequestBody UpdatePackageStatusRequestDTO updatePackageStatusRequestDTO) {
        logger.info("Iniciando atualização de status para o pacote com id: {}. Dados recebidos: {}", id, updatePackageStatusRequestDTO);
        try {
            logger.info("Atualizando status do pacote {}. Novo status: {}", id, updatePackageStatusRequestDTO.getStatus());
            PackageResponseDTO updatedPackage = packageService.updatePackageStatus(id, updatePackageStatusRequestDTO);
            logger.info("Status do pacote atualizado com sucesso. Id: {}. Novo status: {}", id, updatedPackage);
            return ResponseEntity.ok()
                .header("Cache-Control", "no-cache") 
                .body(updatedPackage);
        } catch (IllegalArgumentException e) {
            logger.error("Falha na atualização do status do pacote com id: {}. Motivo: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<CancelResponseDTO> cancelPackage(@PathVariable UUID id) {
        logger.info("Iniciando cancelamento do pacote com id: {}", id);
        try {
            CancelResponseDTO cancelledPackage = packageService.cancelPackage(id);
            logger.info("Cancelamento concluído para pacote com id: {}. Novo status: {}", id, cancelledPackage.getStatus());
            return ResponseEntity.ok()
                .header("Cache-Control", "no-cache") 
                .body(cancelledPackage);
        } catch (IllegalArgumentException e) {
            logger.error("Erro no cancelamento para pacote com id: {}. Motivo: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}