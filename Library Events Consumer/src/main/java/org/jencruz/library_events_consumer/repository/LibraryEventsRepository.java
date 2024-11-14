package org.jencruz.library_events_consumer.repository;

import org.jencruz.library_events_consumer.model.LibraryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryEventsRepository extends JpaRepository<LibraryEvent, Integer> {
}
