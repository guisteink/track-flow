package com.example.track_flow.service;

import com.example.track_flow.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookNotifier {

    private static final Logger logger = LoggerFactory.getLogger(WebhookNotifier.class);

    private final RestTemplate restTemplate;

    @Value("${webhook.url:}")
    private String webhookUrl;

    public WebhookNotifier(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async("taskExecutor")
    public void notifyEvent(Event event) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            logger.warn("Webhook URL não configurada, ignorando notificação.");
            return;
        }

        // Criamos uma instância de HttpHeaders para definir os metadados da requisição HTTP.
        // Aqui, definimos o tipo de conteúdo como JSON, garantindo que o servidor interprete o corpo da mensagem corretamente.
        // Em seguida, construímos uma HttpEntity que encapsula tanto o payload (o objeto 'event') quanto os headers,
        // permitindo que a requisição seja enviada com as informações apropriadas.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Event> request = new HttpEntity<>(event, headers);
        
        // Chama o webhook utilizando HTTP POST com o payload 'event'
        try {
            // restTemplate.exchange envia a requisição com a URL, método, dados e tipo esperado na resposta.
            ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl,       // URL destino
                HttpMethod.POST,  // HTTP POST
                request,          // Entidade que encapsula o payload e os headers
                String.class      // Tipo da resposta esperada
            );
            logger.info("Notificação enviada. Código de resposta: {}", response.getStatusCode());
        } catch (Exception ex) {
            logger.error("Erro ao enviar notificação webhook. Mensagem: {}", ex.getMessage(), ex);
        }
    }
}