package com.example.track_flow.service;

import com.example.track_flow.dto.PackageRequestDTO;
import com.example.track_flow.dto.PackageResponseDTO;
import com.example.track_flow.model.Package;
import com.example.track_flow.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PackageService {

    private static final Logger logger = LoggerFactory.getLogger(PackageService.class);

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private FunFactService funFactService;

    public PackageResponseDTO createPackage(PackageRequestDTO packageRequestDTO) {
        logger.info("Iniciando criação do pacote com dados: {}", packageRequestDTO);

        validatePackageRequest(packageRequestDTO);

        String formattedDate = formatEstimatedDeliveryDate(packageRequestDTO.getEstimatedDeliveryDate());
        boolean isHoliday = holidayService.checkIfHoliday(formattedDate);
        String funFact = funFactService.getDogFunFact();

        Package packageRequest = new Package();
        packageRequest.setDescription(packageRequestDTO.getDescription());
        packageRequest.setSender(packageRequestDTO.getSender());
        packageRequest.setRecipient(packageRequestDTO.getRecipient());
        packageRequest.setHoliday(isHoliday);
        packageRequest.setFunFact(funFact);
        packageRequest.setStatus(Package.Status.CREATED);
        packageRequest.setCreatedAt(LocalDateTime.now());
        packageRequest.setUpdatedAt(LocalDateTime.now());

        Package savedPackage = packageRepository.save(packageRequest);
        logger.info("Pacote salvo com sucesso: {}", savedPackage);

        return mapToResponseDTO(savedPackage);
    }

    private void validatePackageRequest(PackageRequestDTO packageRequestDTO) {
        // Add validations here
        logger.info("Validando dados do pacote: {}", packageRequestDTO);
    }

    private String formatEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = estimatedDeliveryDate.format(formatter);
        logger.info("Data de entrega estimada formatada: {}", formattedDate);
        return formattedDate;
    }

    private PackageResponseDTO mapToResponseDTO(Package savedPackage) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        PackageResponseDTO responseDTO = new PackageResponseDTO();
        responseDTO.setId(savedPackage.getId().toString());
        responseDTO.setDescription(savedPackage.getDescription());
        responseDTO.setSender(savedPackage.getSender());
        responseDTO.setRecipient(savedPackage.getRecipient());
        responseDTO.setStatus(savedPackage.getStatus().name());
        responseDTO.setCreatedAt(savedPackage.getCreatedAt().format(formatter));
        responseDTO.setUpdatedAt(savedPackage.getUpdatedAt().format(formatter));
        if (savedPackage.getDeliveredAt() != null) {
            responseDTO.setDeliveredAt(savedPackage.getDeliveredAt().format(formatter));
        }
        logger.info("Mapeando pacote salvo para DTO de resposta: {}", responseDTO);
        return responseDTO;
    }

    public List<Package> getAllPackages() {
        logger.info("Buscando todos os pacotes");
        return packageRepository.findAll();
    }

    public List<String> getAllEvents() {
        logger.info("Buscando todos os eventos");
        List<String> events = new ArrayList<>();
        events.add("Package created");
        events.add("Package updated");
        events.add("Package delivered");
        return events;
    }

    public PackageResponseDTO updatePackageStatus(String id, String status) {
        Optional<Package> optionalPackage = packageRepository.findById(id);
        if (optionalPackage.isPresent()) {
            Package pkg = optionalPackage.get();
            Package.Status newStatus = Package.Status.valueOf(status);

            if (isValidStatusTransition(pkg.getStatus(), newStatus)) {
                pkg.setStatus(newStatus);
                if (newStatus == Package.Status.DELIVERED) {
                    pkg.setDeliveredAt(LocalDateTime.now());
                }
                pkg.setUpdatedAt(LocalDateTime.now());
                Package updatedPackage = packageRepository.save(pkg);
                return mapToResponseDTO(updatedPackage);
            } else {
                throw new IllegalArgumentException("Invalid status transition");
            }
        } else {
            throw new IllegalArgumentException("Package not found");
        }
    }

    private boolean isValidStatusTransition(Package.Status currentStatus, Package.Status newStatus) {
        if (currentStatus == Package.Status.CREATED && newStatus == Package.Status.IN_TRANSIT) {
            return true;
        } else if (currentStatus == Package.Status.IN_TRANSIT && newStatus == Package.Status.DELIVERED) {
            return true;
        }
        return false;
    }
}