package com.example.track_flow.service;

import com.example.track_flow.model.DogFactResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Serviço que fornece fatos divertidos sobre cães.
 *
 * Esta classe utiliza RestTemplate para realizar uma requisição HTTP ao endpoint "https://dogapi.dog/api/v2/facts",
 * obtendo uma lista de fatos sobre cães a partir de um serviço externo.
 *
 * Método getDogFunFact():
 * Faz uma requisição do tipo GET ao endpoint e processa a resposta. Se a resposta não for nula, 
 * se contiver uma lista de dados e se essa lista não estiver vazia, o método retorna o corpo do primeiro fato encontrado.
 * Caso contrário, retorna a mensagem "No fun fact available.".
 *
 * @return Uma string contendo um fato divertido sobre cães ou uma mensagem padrão caso nenhum fato esteja disponível.
 */
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