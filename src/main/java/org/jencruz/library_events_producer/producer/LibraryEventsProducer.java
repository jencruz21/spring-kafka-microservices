package org.jencruz.library_events_producer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jencruz.library_events_producer.dto.LibraryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<SendResult<Integer, String>> sendLibrary(LibraryEvent libraryEvent) throws JsonProcessingException {
        var key = libraryEvent.getEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);
        var future = template.send(topic, key, value);

        return future.whenComplete((integerStringSendResult, throwable) -> {
            if(throwable != null) {
                handleFailure(key, value, throwable);
            } else {
                handleSuccess(key, value, throwable);
            }
        });
    }

    private void handleSuccess(Integer key, String value, Throwable throwable) {
        log.info("Success: key-{}; value-{}; exception-{}", key, value, throwable);
    }

    private void handleFailure(Integer key, String value, Throwable throwable) {
        log.error("Error: key-{}; value-{}; exception-{}", key, value, throwable);
    }
}
