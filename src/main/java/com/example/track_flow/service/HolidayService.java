package com.example.track_flow.service;

import com.example.track_flow.model.Holiday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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