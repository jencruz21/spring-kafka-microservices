package org.jencruz.library_events_consumer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;

// Manual Configuration
@Profile(value = "dev")
@Configuration
@EnableKafka
public class LibraryEventsConsumerConfiguration { }
