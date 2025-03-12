package com.example.track_flow.service;

import com.example.track_flow.model.Package;
import com.example.track_flow.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Package createPackage(Package packageRequest) {
        // Verificar se a data é um feriado (usando Nager.Date API)
        // boolean isHoliday = checkIfHoliday(packageRequest.getEstimatedDeliveryDate());
        boolean isHoliday = true;
        // Obter um fato curioso sobre cachorros (usando DogAPI)
        String funFact = getDogFunFact();

        // Configurar os dados do pacote
        packageRequest.setHoliday(isHoliday);
        packageRequest.setFunFact(funFact);
        packageRequest.setStatus(Package.Status.CREATED);
        packageRequest.setCreatedAt(LocalDateTime.now());
        packageRequest.setUpdatedAt(LocalDateTime.now());

        // Salvar o pacote no banco de dados
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

    private boolean checkIfHoliday(String date) {
        // Formatar a data para o padrão esperado pela API (yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        String formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Chamar a API Nager.Date para verificar se é feriado
        String url = "https://date.nager.at/api/v3/PublicHolidays/2025/BR";
        Holiday[] holidays = restTemplate.getForObject(url, Holiday[].class);

        if (holidays != null) {
            for (Holiday holiday : holidays) {
                if (holiday.getDate().equals(formattedDate)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getDogFunFact() {
        // Chamar a API DogAPI para obter um fato curioso
        String url = "https://dogapi.dog/api/v2/facts";
        DogFactResponse response = restTemplate.getForObject(url, DogFactResponse.class);

        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
            return response.getData().get(0).getAttributes().getBody();
        }
        return "No fun fact available.";
    }

    // Classes auxiliares para desserialização das APIs
    private static class Holiday {
        private String date;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    private static class DogFactResponse {
        private List<DogFact> data;

        public List<DogFact> getData() {
            return data;
        }

        public void setData(List<DogFact> data) {
            this.data = data;
        }
    }

    private static class DogFact {
        private DogFactAttributes attributes;

        public DogFactAttributes getAttributes() {
            return attributes;
        }

        public void setAttributes(DogFactAttributes attributes) {
            this.attributes = attributes;
        }
    }

    private static class DogFactAttributes {
        private String body;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}