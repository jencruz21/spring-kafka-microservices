package org.jencruz.library_events_consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jencruz.library_events_consumer.model.LibraryEvent;
import org.jencruz.library_events_consumer.repository.LibraryEventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class LibraryEventsService {

    private final LibraryEventsRepository libraryEventsRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public LibraryEventsService(LibraryEventsRepository libraryEventsRepository, ObjectMapper objectMapper) {
        this.libraryEventsRepository = libraryEventsRepository;
        this.objectMapper = objectMapper;
    }

    public void addLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        var libraryEvent = this.objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);

        switch(libraryEvent.getType()) {
            case NEW:
                libraryEvent.getBook().setLibraryEvent(libraryEvent);
                libraryEventsRepository.save(libraryEvent);
                break;
            case UPDATE:
                var updatedLibraryEvent = new LibraryEvent();
                updatedLibraryEvent.setId(libraryEvent.getId());
                updatedLibraryEvent.setType(libraryEvent.getType());
                updatedLibraryEvent.setBook(libraryEvent.getBook());
                updatedLibraryEvent.getBook().setLibraryEvent(updatedLibraryEvent);

                libraryEventsRepository.save(updatedLibraryEvent);
                break;
            default:
                log.info("Error saving the library event in database");
                break;
        }

    }
}
