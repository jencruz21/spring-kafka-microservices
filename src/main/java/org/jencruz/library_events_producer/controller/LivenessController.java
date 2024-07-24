package org.jencruz.library_events_producer.controller;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/library-events/liveness")
public class LivenessController {

    @Data
    @Builder
    public static class Message {
        private String message;
    }

    @GetMapping
    public ResponseEntity<Message> livenessMessage() {
        return ResponseEntity.status(HttpStatus.OK).body(Message.builder().message("Hello World").build());
    }
}
