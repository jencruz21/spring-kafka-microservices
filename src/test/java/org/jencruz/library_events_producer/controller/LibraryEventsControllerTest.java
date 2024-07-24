package org.jencruz.library_events_producer.controller;

import org.apache.kafka.clients.consumer.Consumer;
import org.assertj.core.api.Assertions;
import org.jencruz.library_events_producer.dto.Book;
import org.jencruz.library_events_producer.dto.LibraryEvent;
import org.jencruz.library_events_producer.dto.LibraryEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}",
})
public class LibraryEventsControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;

    @Autowired
    private Consumer<Integer, String> consumer;

    private LibraryEvent libraryEvent;

    @BeforeEach
    public void setUp() {
        Book book = Book
                .builder()
                .id(456)
                .title("Moby Dick")
                .author("Johnny Depp")
                .build();

        libraryEvent = LibraryEvent
                .builder()
                .eventId(null)
                .type(LibraryEventType.NEW)
                .book(book)
                .build();

        var config = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", kafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<Integer, String>(config).createConsumer();
    }

    @Test
    public void postLibraryEventsTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<LibraryEvent> entity = new HttpEntity<>(libraryEvent, headers);

        ResponseEntity<LibraryEvent> response =
                restTemplate.exchange(URI.create("/v1/library-events"), HttpMethod.POST, entity, LibraryEvent.class);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getBody()).isEqualTo(entity.getBody());
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
