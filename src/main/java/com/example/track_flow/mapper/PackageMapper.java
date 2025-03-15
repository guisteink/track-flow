package com.example.track_flow.mapper;

import com.example.track_flow.dto.CancelResponseDTO;
import com.example.track_flow.dto.EventDTO;
import com.example.track_flow.dto.PackageResponseDTO;
import com.example.track_flow.model.Event;
import com.example.track_flow.model.Package;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper responsável por converter a entidade Package para seus respectivos DTOs.
 * Esta classe contém métodos que transformam um objeto Package em diferentes representações
 * de resposta, utilizando comentários e documentação para facilitar a compreensão do fluxo de dados.
 */
@Component
public class PackageMapper {

    
    /**
     * Converte uma entidade Package em um PackageResponseDTO.
     *
     * Este método mapeia os atributos do objeto Package para um objeto PackageResponseDTO,
     * permitindo também a inclusão dos eventos associados caso a flag includeEvents seja verdadeira.
     * Inclui comentários explicando o processo de conversão e o tratamento das propriedades, 
     * como datas e estado do pacote.
     *
     * @param pkg          a entidade Package que será convertida
     * @param includeEvents flag para determinar se os eventos associados deverão ser incluídos no DTO
     * @return um PackageResponseDTO populado com os dados do Package, incluindo os eventos se solicitado
     */
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

    /**
     * Converte uma entidade Package em um CancelResponseDTO.
     *
     * Este método mapeia os atributos essenciais do Package para um DTO de resposta de cancelamento,
     * focando em identificar e retornar o status atual do pacote e o timestamp da última atualização.
     * Comentários foram inseridos para facilitar a compreensão sobre qual atributo é utilizado em cada parte do DTO.
     *
     * @param pkg a entidade Package cujo estado será utilizado para criar o DTO de cancelamento
     * @return um CancelResponseDTO com o ID do pacote, status atual e o timestamp da última atualização
     */
    public CancelResponseDTO toCancelResponseDTO(Package pkg) {
        CancelResponseDTO dto = new CancelResponseDTO();
        dto.setId(pkg.getId().toString());
        dto.setStatus(pkg.getStatus().name());
        dto.setTimestamp(pkg.getUpdatedAt().toString());
        return dto;
    }

    
    /**
     * Mapeia um objeto Event para um EventDTO.
     *
     * Este método privado é responsável por converter um objeto Event em um DTO, associando
     * o identificador do pacote (pkgId) ao evento. Ele ressalta que o campo "date" do DTO receberá 
     * o valor do campo "timestamp" do Event. Comentários adicionais foram incluídos para esclarecer
     * essa correspondência específica entre os atributos.
     *
     * @param event o objeto Event que será convertido
     * @param pkgId o identificador do pacote ao qual o evento pertence
     * @return um EventDTO contendo o identificador do pacote, localização, descrição e timestamp do evento
     */
    private EventDTO mapToEventDTO(Event event, String pkgId) {
        return new EventDTO(
            pkgId,
            event.getLocalization(),
            event.getDescription(),
            event.getTimestamp()  // O campo "date" no DTO receberá o valor de "timestamp" do Event
        );
    }
}