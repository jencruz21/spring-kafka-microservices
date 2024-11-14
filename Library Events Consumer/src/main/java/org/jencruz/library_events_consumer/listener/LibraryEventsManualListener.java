package org.jencruz.library_events_consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Profile(value = "test")
@Component
@Slf4j
public class LibraryEventsManualListener implements AcknowledgingMessageListener<Integer, String> {

    @Override
    @KafkaListener(topics = "library-events")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acknowledgment) {
        log.info("Listening to library-events: {} ", consumerRecord.value());
        if (acknowledgment == null) {
            throw new NullPointerException();
        } else {
            acknowledgment.acknowledge();
        }
    }
}
