package org.jencruz.library_events_producer.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.jencruz.library_events_producer.controller.LibraryEventController;
import org.jencruz.library_events_producer.dto.Book;
import org.jencruz.library_events_producer.dto.LibraryEvent;
import org.jencruz.library_events_producer.dto.LibraryEventType;
import org.jencruz.library_events_producer.producer.LibraryEventsProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(value = LibraryEventController.class)
@AutoConfigureMockMvc
public class LibraryEventsControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LibraryEventsProducer libraryEventsProducer;
    private LibraryEvent libraryEvent;
    private Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(457)
                .title("Test title")
                .author("Test author")
                .build();
    }

    @Test
    public void TestLibraryEventController_CreateLibraryEvent_ReturnIsCreated() throws Exception {
        libraryEvent = LibraryEvent.builder()
                .id(456)
                .type(LibraryEventType.NEW)
                .book(this.book)
                .build();
        String json = objectMapper.writeValueAsString(this.libraryEvent);
        Mockito.when(libraryEventsProducer.sendLibrary(Mockito.isA(LibraryEvent.class)))
                .thenReturn(null);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/library-events")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void TestLibraryEventController_CreateLibraryEvent_ReturnsInvalidValues() throws Exception {
        libraryEvent = LibraryEvent.builder()
                .id(456)
                .type(LibraryEventType.NEW)
                .build();

        String json = objectMapper.writeValueAsString(this.libraryEvent);
        Mockito.when(libraryEventsProducer.sendLibrary(Mockito.isA(LibraryEvent.class)))
                .thenReturn(null);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/v1/library-events")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void TestLibraryEventController_CreateLibraryEvent_Returns4xxMessage() throws Exception {
        libraryEvent = LibraryEvent.builder()
                .id(456)
                .type(LibraryEventType.NEW)
                .build();

        String json = objectMapper.writeValueAsString(this.libraryEvent);
        Mockito.when(libraryEventsProducer.sendLibrary(Mockito.isA(LibraryEvent.class)))
                .thenReturn(null);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/v1/library-events")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string("book must not be null"));
    }
}
