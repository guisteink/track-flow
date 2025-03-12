package com.example.track_flow.service;

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

    public Package createPackage(Package packageRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = packageRequest.getEstimatedDeliveryDate().format(formatter);

        boolean isHoliday = holidayService.checkIfHoliday(formattedDate);
        String funFact = funFactService.getDogFunFact();

        packageRequest.setHoliday(isHoliday);
        packageRequest.setFunFact(funFact);
        packageRequest.setStatus(Package.Status.CREATED);
        packageRequest.setCreatedAt(LocalDateTime.now());
        packageRequest.setUpdatedAt(LocalDateTime.now());

        return packageRepository.save(packageRequest);
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