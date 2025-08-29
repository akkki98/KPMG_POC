package com.example.demo.config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(prefix = "azure.servicebus", name = "connection-string")
public class ServiceBusConfig {

    @Value("${azure.servicebus.connection-string:}")
    private String connectionString;

    @Bean
    public ServiceBusClientBuilder serviceBusClientBuilder() {
        // At this point property exists (non-empty) due to ConditionalOnProperty
        return new ServiceBusClientBuilder().connectionString(connectionString);
    }
}
