package com.example.track_flow.controller;

import com.example.track_flow.dto.CancelResponseDTO;
import com.example.track_flow.dto.PackageRequestDTO;
import com.example.track_flow.dto.PackageResponseDTO;
import com.example.track_flow.dto.UpdatePackageStatusRequestDTO;
import com.example.track_flow.model.Event;
import com.example.track_flow.model.Package;
import com.example.track_flow.service.PackageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PackageResponseDTO> createPackage(@RequestBody PackageRequestDTO packageRequestDTO) {
        logger.info("Iniciando criação de pacote. Requisição recebida: {}", packageRequestDTO);
        PackageResponseDTO createdPackage = packageService.createPackage(packageRequestDTO);
        logger.info("Pacote criado com sucesso. Detalhes do pacote: {}", createdPackage);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPackage);
    }

    @GetMapping
    public ResponseEntity<List<Package>> getAllPackages(
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) String recipient
    ) {
        if ((sender == null || sender.isBlank()) && (recipient == null || recipient.isBlank())) {
            logger.info("Solicitação para listar todos os pacotes iniciada.");
            List<Package> packages = packageService.getAllPackages();
            logger.info("Listagem de pacotes realizada com êxito. Total de pacotes retornados: {}", packages.size());
            return ResponseEntity.ok(packages);
        } else {
            logger.info("Solicitação para listar pacotes com filtro: sender={} e recipient={}", sender, recipient);
            List<Package> packages = packageService.getPackagesByFilter(sender, recipient);
            logger.info("Número de pacotes filtrados: {}", packages.size());
            return ResponseEntity.ok(packages);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Package>> getPackagesByFilter(
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) String recipient
    ) {
        logger.info("Iniciando filtro de pacotes por sender: {} e recipient: {}", sender, recipient);
        List<Package> filteredPackages = packageService.getPackagesByFilter(sender, recipient);
        logger.info("Número de pacotes filtrados: {}", filteredPackages.size());
        return ResponseEntity.ok(filteredPackages);
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<Event>> getPackageEvents(@PathVariable UUID id) {
        logger.info("Iniciando listagem de eventos para o pacote com id: {}", id);
        List<Event> events = packageService.getPackageEvents(id);
        logger.info("Listagem de eventos concluída para o pacote com id: {}. Total de eventos: {}", id, events.size());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackageResponseDTO> getPackageById(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "true") Boolean showEvents
    ) {
        logger.info("Busca de pacote iniciada para o id: {} com parâmetro showEvents={}", id, showEvents);
        PackageResponseDTO packageDto = packageService.getPackageById(id, showEvents);
        if (packageDto != null) {
            logger.info("Pacote encontrado para o id: {}. Detalhes: {}", id, packageDto);
            return ResponseEntity.ok(packageDto);
        } else {
            logger.warn("Nenhum pacote encontrado para o id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PackageResponseDTO> updatePackageStatus(@PathVariable UUID id, @RequestBody UpdatePackageStatusRequestDTO updatePackageStatusRequestDTO) {
        logger.info("Iniciando atualização de status para o pacote com id: {}. Dados recebidos: {}", id, updatePackageStatusRequestDTO);
        try {
            PackageResponseDTO updatedPackage = packageService.updatePackageStatus(id, updatePackageStatusRequestDTO);
            logger.info("Status do pacote atualizado com sucesso. Id: {}. Novo status: {}", id, updatedPackage);
            return ResponseEntity.ok(updatedPackage);
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
            return ResponseEntity.ok(cancelledPackage);
        } catch (IllegalArgumentException e) {
            logger.error("Erro no cancelamento para pacote com id: {}. Motivo: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}