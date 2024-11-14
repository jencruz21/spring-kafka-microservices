package org.jencruz.library_events_producer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.jencruz.library_events_producer.dto.LibraryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class LibraryEventsProducer {

    @Value("${spring.kafka.topic}")
    private String topic;
    private final KafkaTemplate<Integer, String> template;
    private final ObjectMapper objectMapper;

    @Autowired
    public LibraryEventsProducer(KafkaTemplate<Integer, String> template, ObjectMapper objectMapper) {
        this.template = template;
        this.objectMapper = objectMapper;
    }

    public SendResult<Integer, String> sendLibrary(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        var key = libraryEvent.getId();
        var value = objectMapper.writeValueAsString(libraryEvent);

        Iterable<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>(this.topic, null, null, key, value, recordHeaders);
        return template.send(producerRecord).get();
    }
}
