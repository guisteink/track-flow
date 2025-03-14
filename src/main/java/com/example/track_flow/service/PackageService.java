package com.example.track_flow.service;

import com.example.track_flow.dto.CancelResponseDTO;
import com.example.track_flow.dto.EventDTO;
import com.example.track_flow.dto.PackageRequestDTO;
import com.example.track_flow.dto.PackageResponseDTO;
import com.example.track_flow.dto.UpdatePackageStatusRequestDTO;
import com.example.track_flow.exception.InvalidStatusTransitionException;
import com.example.track_flow.exception.PackageNotFoundException;
import com.example.track_flow.mapper.PackageMapper;
import com.example.track_flow.model.Event;
import com.example.track_flow.model.Package;
import com.example.track_flow.repository.EventRepository;
import com.example.track_flow.repository.PackageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.track_flow.service.WebhookNotifier;

@Service
public class PackageService {

    private static final Logger logger = LoggerFactory.getLogger(PackageService.class);
    private final PackageRepository packageRepository;
    private final EventRepository eventRepository;
    private final HolidayService holidayService;
    private final FunFactService funFactService;
    private final PackageMapper packageMapper;
    private final WebhookNotifier webhookNotifier;

    public PackageService(PackageRepository packageRepository,
                          EventRepository eventRepository,
                          HolidayService holidayService,
                          FunFactService funFactService,
                          PackageMapper packageMapper,
                          WebhookNotifier webhookNotifier) {
        this.packageRepository = packageRepository;
        this.eventRepository = eventRepository;
        this.holidayService = holidayService;
        this.funFactService = funFactService;
        this.packageMapper = packageMapper;
        this.webhookNotifier = webhookNotifier;
    }

    public PackageResponseDTO createPackage(PackageRequestDTO packageRequestDTO) {
        logger.info("Iniciando criação do pacote: {}", packageRequestDTO);
        validatePackageRequest(packageRequestDTO);
        String formattedDate = formatEstimatedDeliveryDate(packageRequestDTO.getEstimatedDeliveryDate());
        boolean isHoliday = holidayService.checkIfHoliday(formattedDate);
        String funFact = funFactService.getDogFunFact();

        Package pkg = new Package();
        pkg.setDescription(packageRequestDTO.getDescription());
        pkg.setSender(packageRequestDTO.getSender());
        pkg.setRecipient(packageRequestDTO.getRecipient());
        pkg.setHoliday(isHoliday);
        pkg.setFunFact(funFact);
        pkg.setStatus(Package.Status.CREATED);

        Package savedPackage = packageRepository.save(pkg);
        logger.info("Pacote salvo com sucesso: {}", savedPackage);

        createTrackingEvent(savedPackage, "Pacote chegou ao centro de distribuição", "Centro de Distribuição São Paulo");

        PackageResponseDTO responseDTO = packageMapper.toResponseDTO(savedPackage, false);
        logger.info("Retornando resposta DTO: {}", responseDTO);
        return responseDTO;
    }

    private void validatePackageRequest(PackageRequestDTO packageRequestDTO) {
        /* Validar headers, e dados da regra de negocio */
        logger.info("Validando dados do pacote: {}", packageRequestDTO);
    }

    private String formatEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = estimatedDeliveryDate.format(formatter);
        logger.info("Data formatada: {}", formattedDate);
        return formattedDate;
    }

    @Cacheable(value = "packages", key = "'all'")
    public List<Package> getAllPackages() {
        logger.info("Buscando todos os pacotes cadastrados.");
        List<Package> packages = packageRepository.findAll();
        logger.info("Total de pacotes encontrados: {}", packages.size());
        return packages;
    }

    public List<Event> getPackageEvents(UUID packageId) {
        logger.info("Buscando eventos para o pacote com id: {}", packageId);
        Optional<Package> optionalPackage = packageRepository.findById(packageId);
        if (optionalPackage.isPresent()) {
            List<Event> events = optionalPackage.get().getEvents();
            logger.info("Total de eventos encontrados: {}", events.size());
            return events;
        } else {
            logger.error("Pacote não encontrado para id: {}", packageId);
            throw new PackageNotFoundException("Package not found for id: " + packageId);
        }
    }

    @CacheEvict(value = "packages", key = "#id")
    public PackageResponseDTO updatePackageStatus(UUID id, UpdatePackageStatusRequestDTO updateRequest) {
        logger.info("Iniciando atualização para pacote com id: {}", id);
        Optional<Package> optionalPackage = packageRepository.findById(id);
        if (optionalPackage.isPresent()) {
            Package pkg = optionalPackage.get();
            Package.Status oldStatus = pkg.getStatus();
            Package.Status newStatus = Package.Status.valueOf(updateRequest.getStatus());
            logger.info("Status atual: {}, Novo status: {}", oldStatus, newStatus);

            if (isValidStatusTransition(oldStatus, newStatus)) {
                pkg.setStatus(newStatus);
                if (newStatus == Package.Status.DELIVERED) {
                    pkg.setDeliveredAt(LocalDateTime.now());
                    logger.info("Pacote marcado como entregue em: {}", pkg.getDeliveredAt());
                }
                pkg.setUpdatedAt(LocalDateTime.now());

                Package updatedPackage = packageRepository.save(pkg);
                logger.info("Status atualizado com sucesso para: {}", updatedPackage.getStatus());

                createTrackingEvent(updatedPackage, "Status changed from " + oldStatus + " to " + newStatus, "System Update");

                return packageMapper.toResponseDTO(updatedPackage, true);
            } else {
                logger.error("Transição inválida do status: {} para {}", oldStatus, newStatus);
                throw new InvalidStatusTransitionException("Invalid status transition from " +
                        oldStatus + " to " + newStatus);
            }
        } else {
            logger.error("Pacote não encontrado para id: {}", id);
            throw new PackageNotFoundException("Package not found for id: " + id);
        }
    }

    private boolean isValidStatusTransition(Package.Status currentStatus, Package.Status newStatus) {
        boolean valid = (currentStatus == Package.Status.CREATED && newStatus == Package.Status.IN_TRANSIT) ||
                        (currentStatus == Package.Status.IN_TRANSIT && newStatus == Package.Status.DELIVERED);
        logger.debug("Verificando transição de {} para {}: {}", currentStatus, newStatus, valid);
        return valid;
    }

    @Cacheable(value = "packages", key = "#id")
    public PackageResponseDTO getPackageById(UUID id, Boolean showEvents) {
        logger.info("Buscando pacote com id: {}", id);
        Optional<Package> optionalPackage = packageRepository.findById(id);
        if (optionalPackage.isEmpty()) {
            logger.error("Pacote não encontrado para id: {}", id);
            throw new PackageNotFoundException("Package not found for id: " + id);
        }
        Package pkg = optionalPackage.get();
        if (Boolean.FALSE.equals(showEvents)) {
            logger.debug("Ocultando eventos para pacote com id: {}", id);
            pkg.setEvents(null);
        }
        PackageResponseDTO response = packageMapper.toResponseDTO(pkg, true);
        logger.info("Retornando DTO para pacote {}", response);
        return response;
    }

    public CancelResponseDTO cancelPackage(UUID id) {
        logger.info("Iniciando cancelamento para pacote com id: {}", id);
        Optional<Package> optionalPackage = packageRepository.findById(id);
        if (optionalPackage.isEmpty()) {
            logger.error("Pacote não encontrado para id: {}", id);
            throw new PackageNotFoundException("Package not found for id: " + id);
        }
        Package pkg = optionalPackage.get();
        if (pkg.getStatus() != Package.Status.CREATED) {
            logger.error("Cancelamento não permitido. Status atual: {}", pkg.getStatus());
            throw new InvalidStatusTransitionException("Only packages that have not left for delivery can be cancelled");
        }
        Package.Status oldStatus = pkg.getStatus();
        pkg.setStatus(Package.Status.CANCELED);
        pkg.setUpdatedAt(LocalDateTime.now());
        Package updatedPackage = packageRepository.save(pkg);
        logger.info("Pacote cancelado com sucesso: {}", updatedPackage);

        createTrackingEvent(updatedPackage, "Package cancelled (status changed from " + oldStatus + " to CANCELED)", "Cancellation Endpoint");

        return packageMapper.toCancelResponseDTO(updatedPackage);
    }

    public List<Package> getPackagesByFilter(String sender, String recipient) {
        logger.info("Filtrando pacotes pelo sender: {} e recipient: {}", sender, recipient);
        List<Package> packages = packageRepository.findAll();
        return packages.stream()
                .filter(pkg -> {
                    boolean match = true;
                    if (sender != null && !sender.isBlank()) {
                        match = pkg.getSender() != null && pkg.getSender().equalsIgnoreCase(sender);
                    }
                    if (recipient != null && !recipient.isBlank()) {
                        match = match && pkg.getRecipient() != null && pkg.getRecipient().equalsIgnoreCase(recipient);
                    }
                    return match;
                })
                .toList();
    }

    private void createTrackingEvent(Package pkg, String description, String localization) {
        Event event = new Event();
        event.setPkg(pkg);
        event.setDescription(description);
        event.setLocalization(localization);
        eventRepository.save(event);
        logger.info("Evento criado: {}", event);
        webhookNotifier.notifyEvent(event);
    }
}