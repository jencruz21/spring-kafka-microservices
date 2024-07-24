package org.jencruz.library_events_producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.assertj.core.api.Assertions;
import org.jencruz.library_events_producer.dto.Book;
import org.jencruz.library_events_producer.dto.LibraryEvent;
import org.jencruz.library_events_producer.dto.LibraryEventType;
import org.junit.jupiter.api.AfterEach;
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

    private LibraryEvent libraryEvent;

    @Autowired
    private TestRestTemplate restTemplate;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;

    private Consumer<Integer, String> consumer;

    @Autowired
    private ObjectMapper objectMapper;
    

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

        Map<String, Object> config = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", kafkaBroker));
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer = new DefaultKafkaConsumerFactory<Integer, String>(config, new IntegerDeserializer(), new StringDeserializer())
                .createConsumer();

        kafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
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

        ConsumerRecords<Integer, String> record = KafkaTestUtils.getRecords(consumer);
        assert record.count() == 1;

        record.forEach(integerStringConsumerRecord -> {
            try {
                LibraryEvent result = objectMapper.readValue(integerStringConsumerRecord.value(), LibraryEvent.class);
                Assertions.assertThat(result).isEqualTo(libraryEvent);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
