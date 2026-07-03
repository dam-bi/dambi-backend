package studio.aroudhub.ticketing.domain.event.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import studio.aroudhub.ticketing.domain.event.repository.EventList;
import studio.aroudhub.ticketing.domain.event.service.EventService;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    void getEvents_returnsEventListFromService() throws Exception {
        List<EventList> fakeEvents = List.of(
                new EventList(
                        1,
                        101,
                        "Early bird discount",
                        "Discount event for early reservations"
                )
        );

        when(eventService.findAll()).thenReturn(fakeEvents);

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].eventId").value(1))
                .andExpect(jsonPath("$[0].concertId").value(101))
                .andExpect(jsonPath("$[0].title").value("Early bird discount"))
                .andExpect(jsonPath("$[0].description").value("Discount event for early reservations"));

        verify(eventService).findAll();
    }
}
