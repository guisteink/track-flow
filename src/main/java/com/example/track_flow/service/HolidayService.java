package com.example.track_flow.service;

import com.example.track_flow.model.Holiday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Verifica se uma data específica corresponde a um feriado público.
 *
 * <p>Este método recebe uma data no formato "dd/MM/yyyy", converte-a para o padrão "yyyy-MM-dd"
 * e consulta a API externa "https://date.nager.at/api/v3/PublicHolidays/2025/BR" para obter os
 * feriados públicos do ano de 2025 no Brasil. Em seguida, itera sobre os feriados retornados e
 * verifica se a data formatada corresponde a algum deles, retornando true se houver correspondência
 * e false caso contrário.
 *
 * @param date a data a ser verificada no formato "dd/MM/yyyy"
 * @return true se a data corresponder a um feriado público, false caso contrário
 */
@Service
public class HolidayService {

    @Autowired
    private RestTemplate restTemplate;

    public boolean checkIfHoliday(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        String formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

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
}