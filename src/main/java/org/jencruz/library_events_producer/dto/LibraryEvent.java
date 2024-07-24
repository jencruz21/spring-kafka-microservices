package org.jencruz.library_events_producer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LibraryEvent {
    private Integer eventId;
    private LibraryEventType type;
    private Book book;
}
