package org.jencruz.library_events_consumer.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jencruz.library_events_consumer.service.LibraryEventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.stereotype.Component;

@Profile(value = "dev")
@Component
@Slf4j
public class LibraryEventsListener {

    private final LibraryEventsService libraryEventsService;

    @Autowired
    public LibraryEventsListener(LibraryEventsService libraryEventsService) {
        this.libraryEventsService = libraryEventsService;
    }

    @KafkaListener(topics = "library-events")
    public void listen(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("Listening to library-events: {} ", consumerRecord.value());
        libraryEventsService.addLibraryEvent(consumerRecord);
    }
}
