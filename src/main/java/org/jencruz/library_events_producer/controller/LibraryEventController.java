package org.jencruz.library_events_producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.jencruz.library_events_producer.dto.LibraryEvent;
import org.jencruz.library_events_producer.producer.LibraryEventsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/library-events")
@Slf4j
public class LibraryEventController {

    private final LibraryEventsProducer libraryEventsProducer;

    @Autowired
    public LibraryEventController(LibraryEventsProducer libraryEventsProducer) {
        this.libraryEventsProducer = libraryEventsProducer;
    }

    @PostMapping
    public ResponseEntity<LibraryEvent> createLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
        log.info("LibraryEvent Request: {} ", libraryEvent);
        libraryEventsProducer.sendLibrary(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
