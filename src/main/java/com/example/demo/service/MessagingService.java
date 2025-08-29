package com.example.demo.service;

import com.azure.messaging.servicebus.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * MessagingService sends JSON messages representing user registrations to an Azure Service Bus queue.
 * On repeated failure it publishes the message to the queue's dead-letter subqueue and logs the event.
 */
// @Service // Disabled for now
@RequiredArgsConstructor
@Slf4j
public class MessagingService {

    // private final ServiceBusClientBuilder serviceBusClientBuilder; // Disabled
    private final ObjectMapper objectMapper; // Could still be used if re-enabled

    @Value("${azure.servicebus.queue.user-registration:user-registration}")
    private String userRegistrationQueue;

    // private ServiceBusSenderClient buildMainSender() { return null; }
    // private ServiceBusSenderClient buildDeadLetterSender() { return null; }

    /**
     * Sends a user registration payload as JSON to Service Bus.
     * Retries transient failures; on recovery publishes to dead-letter subqueue.
     * @param payload arbitrary DTO (e.g., UserRegistrationDto)
     */
    // Messaging disabled: no-op replacement
    public void sendUserRegistration(Object payload) {
        log.debug("Messaging disabled: skipping sendUserRegistration");
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize payload to JSON", e);
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
