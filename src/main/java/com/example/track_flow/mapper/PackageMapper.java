package com.example.track_flow.mapper;

import com.example.track_flow.dto.CancelResponseDTO;
import com.example.track_flow.dto.EventDTO;
import com.example.track_flow.dto.PackageResponseDTO;
import com.example.track_flow.model.Event;
import com.example.track_flow.model.Package;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PackageMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public PackageResponseDTO toResponseDTO(Package pkg, boolean includeEvents) {
        PackageResponseDTO dto = new PackageResponseDTO();
        dto.setId(pkg.getId().toString());
        dto.setDescription(pkg.getDescription());
        dto.setSender(pkg.getSender());
        dto.setRecipient(pkg.getRecipient());
        dto.setStatus(pkg.getStatus().name());
        dto.setCreatedAt(pkg.getCreatedAt().format(FORMATTER));
        dto.setUpdatedAt(pkg.getUpdatedAt().format(FORMATTER));
        if (pkg.getDeliveredAt() != null) {
            dto.setDeliveredAt(pkg.getDeliveredAt().format(FORMATTER));
        }
        if (includeEvents && pkg.getEvents() != null) {
            List<EventDTO> eventDTOs = pkg.getEvents().stream()
                .map(event -> mapToEventDTO(event, pkg.getId().toString()))
                .collect(Collectors.toList());
            dto.setEvents(eventDTOs);
        }
        return dto;
    }

    public CancelResponseDTO toCancelResponseDTO(Package pkg) {
        CancelResponseDTO response = new CancelResponseDTO();
        response.setId(pkg.getId().toString());
        response.setStatus(pkg.getStatus().name());
        response.setTimestamp(pkg.getUpdatedAt().format(FORMATTER));
        return response;
    }

    private EventDTO mapToEventDTO(Event event, String pkgId) {
        String eventTimestamp = event.getTimestamp().format(FORMATTER);
        return new EventDTO(pkgId,
                event.getLocalization(),
                event.getDescription(),
                eventTimestamp);
    }
}