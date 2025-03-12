package com.example.track_flow.service;

import com.example.track_flow.dto.PackageRequestDTO;
import com.example.track_flow.dto.PackageResponseDTO;
import com.example.track_flow.model.Package;
import com.example.track_flow.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private FunFactService funFactService;

    public PackageResponseDTO createPackage(PackageRequestDTO packageRequestDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = packageRequestDTO.getEstimatedDeliveryDate().format(formatter);

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

        PackageResponseDTO responseDTO = new PackageResponseDTO();
        responseDTO.setId(savedPackage.getId());
        responseDTO.setDescription(savedPackage.getDescription());
        responseDTO.setSender(savedPackage.getSender());
        responseDTO.setRecipient(savedPackage.getRecipient());
        responseDTO.setStatus(savedPackage.getStatus().name());
        responseDTO.setCreatedAt(savedPackage.getCreatedAt());
        responseDTO.setUpdatedAt(savedPackage.getUpdatedAt());

        return responseDTO;
    }

    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    public List<String> getAllEvents() {
        List<String> events = new ArrayList<>();
        events.add("Package created");
        events.add("Package updated");
        events.add("Package delivered");
        return events;
    }
}