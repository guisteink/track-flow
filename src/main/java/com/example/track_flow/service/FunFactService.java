package com.example.track_flow.service;

import com.example.track_flow.model.DogFactResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FunFactService {

    @Autowired
    private RestTemplate restTemplate;

    public String getDogFunFact() {
        String url = "https://dogapi.dog/api/v2/facts";
        DogFactResponse response = restTemplate.getForObject(url, DogFactResponse.class);

        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
            return response.getData().get(0).getAttributes().getBody();
        }
        return "No fun fact available.";
    }
}