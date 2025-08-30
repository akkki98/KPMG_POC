package com.example.demo.service;

import com.azure.messaging.servicebus.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Consumes user-related events. On repeated processing failure a message is dead-lettered.
 */
@Component
@ConditionalOnProperty(prefix = "azure.servicebus.consumer", name = "enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class UserEventConsumer {

    private final ServiceBusProcessorClient processorClient;
    private final ObjectMapper objectMapper;
    private final int maxAttempts;
    private final String failTestEmail;

    public UserEventConsumer(ServiceBusClientBuilder builder,
                             ObjectMapper objectMapper,
                             @Value("${azure.servicebus.queue.user-registration}") String queueName,
                             @Value("${azure.servicebus.consumer.max-delivery-attempts:5}") int maxAttempts,
                             @Value("${azure.servicebus.consumer.fail-test-email:}") String failTestEmail) {
        this.objectMapper = objectMapper;
        this.maxAttempts = maxAttempts;
        this.failTestEmail = failTestEmail;
        this.processorClient = builder
                .processor()
                .queueName(queueName)
                .processMessage(this::processMessage)
                .processError(this::processError)
                .buildProcessorClient();
        this.processorClient.start();
        log.info("UserEventConsumer started for queue {} (maxAttempts={}, failTestEmail='{}')", queueName, maxAttempts, failTestEmail);
    }

    private void processMessage(ServiceBusReceivedMessageContext context) {
        ServiceBusReceivedMessage msg = context.getMessage();
        long delivery = msg.getDeliveryCount();
        String body = msg.getBody().toString();
        String eventType = (String) msg.getApplicationProperties().getOrDefault("eventType", "unknown");
        try {
            log.info("Processing eventType={} deliveryCount={} messageId={}", eventType, delivery, msg.getMessageId());
            if (delivery >= maxAttempts) {
                log.warn("Max attempts reached ({}). Dead-lettering message {}", maxAttempts, msg.getMessageId());
                context.deadLetter();
                return;
            }
            handle(eventType, body, msg);
        } catch (Exception ex) {
            log.error("Failure processing messageId={} attempt={} err={}", msg.getMessageId(), delivery, ex.getMessage(), ex);
            throw ex; // abandon for retry
        }
    }

    private void handle(String eventType, String body, ServiceBusReceivedMessage msg) {
        JsonNode node;
        try {
            node = objectMapper.readTree(body);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON payload", e);
        }
        // Inject test failure based on email value
        String email = node.path("email").asText();
        if (!failTestEmail.isBlank() && failTestEmail.equalsIgnoreCase(email)) {
            throw new RuntimeException("Intentional test failure for email " + email);
        }
        switch (eventType) {
            case "user_registered" -> handleUserRegistered(node);
            case "user_approved" -> handleUserApproved(node);
            default -> log.warn("Unknown eventType={} messageId={}", eventType, msg.getMessageId());
        }
    }

    private void handleUserRegistered(JsonNode node) {
        log.info("User registered event payload: id={} email={} status={} ",
                node.path("id").asText(), node.path("email").asText(), node.path("status").asText());
        // Add workflow initiation logic here
    }

    private void handleUserApproved(JsonNode node) {
        log.info("User approved event payload: id={} email={} status={} ",
                node.path("id").asText(), node.path("email").asText(), node.path("status").asText());
        // Add provisioning logic here
    }

    private void processError(ServiceBusErrorContext context) {
        log.error("Processor level error source={} entity={} exception={}",
                context.getErrorSource(),
                context.getFullyQualifiedNamespace()+"/"+context.getEntityPath(),
                context.getException().getMessage(),
                context.getException());
    }
}
