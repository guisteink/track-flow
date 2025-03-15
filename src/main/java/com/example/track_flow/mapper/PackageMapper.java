package com.example.track_flow.mapper;

import com.example.track_flow.dto.CancelResponseDTO;
import com.example.track_flow.dto.EventDTO;
import com.example.track_flow.dto.PackageResponseDTO;
import com.example.track_flow.model.Event;
import com.example.track_flow.model.Package;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PackageMapper {

    public PackageResponseDTO toResponseDTO(Package pkg, boolean includeEvents) {
        PackageResponseDTO dto = new PackageResponseDTO();
        dto.setId(pkg.getId().toString());
        dto.setDescription(pkg.getDescription());
        dto.setSender(pkg.getSender());
        dto.setRecipient(pkg.getRecipient());
        dto.setStatus(pkg.getStatus().name());
        dto.setCreatedAt(pkg.getCreatedAt().toString());
        dto.setUpdatedAt(pkg.getUpdatedAt().toString());
        if (pkg.getDeliveredAt() != null) {
            dto.setDeliveredAt(pkg.getDeliveredAt().toString());
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
        CancelResponseDTO dto = new CancelResponseDTO();
        dto.setId(pkg.getId().toString());
        dto.setStatus(pkg.getStatus().name());
        dto.setTimestamp(pkg.getUpdatedAt().toString());
        return dto;
    }

    private EventDTO mapToEventDTO(Event event, String pkgId) {
        return new EventDTO(
            pkgId,
            event.getLocalization(),
            event.getDescription(),
            event.getTimestamp()  // O campo "date" no DTO receberá o valor de "timestamp" do Event
        );
    }
}