package studio.aroudhub.ticketing.domain.event.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import studio.aroudhub.ticketing.domain.event.repository.ConcertResponse;
import studio.aroudhub.ticketing.domain.event.repository.EventWithConcertResponse;
import studio.aroudhub.ticketing.domain.event.service.EventService;
import studio.aroudhub.ticketing.global.exception.GlobalExceptionHandler;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("testWithoutDB")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    void getEvents_returnsEventWithConcertResponseAsJson() throws Exception {
        List<EventWithConcertResponse> fakeEvents = List.of(
                new EventWithConcertResponse(
                        1,
                        "Early bird discount",
                        "Discount event for early reservations",
                        "SCHEDULED",
                        LocalDate.of(2026, 7, 5),
                        LocalDate.of(2026, 7, 31),
                        new ConcertResponse(
                                101,
                                "Summer Festival",
                                "https://cdn.example.com/concerts/101.png",
                                "Three day festival",
                                "Olympic Hall",
                                120,
                                LocalDate.of(2026, 7, 20),
                                LocalDate.of(2026, 7, 22),
                                "15+"
                        )
                )
        );

        when(eventService.findAllWithConcert()).thenReturn(fakeEvents);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].eventId").value(1))
                .andExpect(jsonPath("$[0].eventTitle").value("Early bird discount"))
                .andExpect(jsonPath("$[0].eventDesc").value("Discount event for early reservations"))
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"))
                .andExpect(jsonPath("$[0].eventStartDate").value("2026-07-05"))
                .andExpect(jsonPath("$[0].eventEndDate").value("2026-07-31"))
                .andExpect(jsonPath("$[0].concert.concertId").value(101))
                .andExpect(jsonPath("$[0].concert.concertTitle").value("Summer Festival"))
                .andExpect(jsonPath("$[0].concert.imgUrl").value("https://cdn.example.com/concerts/101.png"))
                .andExpect(jsonPath("$[0].concert.concertDesc").value("Three day festival"))
                .andExpect(jsonPath("$[0].concert.venue").value("Olympic Hall"))
                .andExpect(jsonPath("$[0].concert.runningTime").value(120))
                .andExpect(jsonPath("$[0].concert.concertStartDate").value("2026-07-20"))
                .andExpect(jsonPath("$[0].concert.concertEndDate").value("2026-07-22"))
                .andExpect(jsonPath("$[0].concert.ageRating").value("15+"));

        verify(eventService).findAllWithConcert();
    }

    @Test
    void getEvents_returnsBadRequestJsonWhenServiceThrowsIllegalArgumentException() throws Exception {
        doThrow(new IllegalArgumentException("Event lookup failed."))
                .when(eventService)
                .findAllWithConcert();

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("Event lookup failed."));

        verify(eventService).findAllWithConcert();
    }
}
