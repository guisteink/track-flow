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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Event> request = new HttpEntity<>(event, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(webhookUrl, HttpMethod.POST, request, String.class);
            logger.info("Notificação enviada. Código de resposta: {}", response.getStatusCode());
        } catch (Exception ex) {
            logger.error("Erro ao enviar notificação webhook. Mensagem: {}", ex.getMessage(), ex);
        }
    }
}