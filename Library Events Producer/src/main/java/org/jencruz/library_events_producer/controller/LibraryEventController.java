package org.jencruz.library_events_producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jencruz.library_events_producer.dto.LibraryEvent;
import org.jencruz.library_events_producer.dto.LibraryEventType;
import org.jencruz.library_events_producer.producer.LibraryEventsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

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
    public ResponseEntity<LibraryEvent> createLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        log.info("LibraryEvent Request: {} ", libraryEvent);
        libraryEventsProducer.sendLibrary(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping
    public ResponseEntity<?> updateLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws Exception {
        if(libraryEvent.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please insert an library event id");
        }

        if(!libraryEvent.getType().equals(LibraryEventType.UPDATE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Library Event type is not UPDATE");
        } else {
            libraryEventsProducer.sendLibrary(libraryEvent);
            return new ResponseEntity<>(libraryEvent, HttpStatus.OK);
        }
    }
}
