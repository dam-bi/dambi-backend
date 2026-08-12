package studio.aroudhub.ticketing.domain.event.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.event.repository.response.ConcertResponse;
import studio.aroudhub.ticketing.domain.event.repository.response.EventDetailResponse;
import studio.aroudhub.ticketing.domain.event.repository.response.EventListResponse;
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
    void getEvents_returnsPagedEventListResponseAsJson() throws Exception {
        Page<EventListResponse> fakeEvents = new PageImpl<>(List.of(
                new EventListResponse(
                        1,
                        "Early bird discount",
                        "https://cdn.example.com/concerts/101.png",
                        "SCHEDULED",
                        LocalDate.of(2026, 7, 5),
                        LocalDate.of(2026, 7, 31)
                )
        ));

        when(eventService.findPage(any(), isNull(), isNull())).thenReturn(fakeEvents);

        // Page 응답은 content 배열 아래에 실제 목록이 직렬화된다.
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].eventId").value(1))
                .andExpect(jsonPath("$.content[0].eventTitle").value("Early bird discount"))
                .andExpect(jsonPath("$.content[0].concertImg").value("https://cdn.example.com/concerts/101.png"))
                .andExpect(jsonPath("$.content[0].status").value("SCHEDULED"))
                .andExpect(jsonPath("$.content[0].eventStartDate").value("2026-07-05"))
                .andExpect(jsonPath("$.content[0].eventEndDate").value("2026-07-31"));

        verify(eventService).findPage(any(), isNull(), isNull());
    }

    @Test
    void getEvents_returnsBadRequestJsonWhenServiceThrowsIllegalArgumentException() throws Exception {
        doThrow(new IllegalArgumentException("Event lookup failed."))
                .when(eventService)
                .findPage(any(), isNull(), isNull());

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("Event lookup failed."));

        verify(eventService).findPage(any(), isNull(), isNull());
    }

    @Test
    void getEvents_withSortByAndStatus_passesQueryParametersToService() throws Exception {
        Page<EventListResponse> fakeEvents = new PageImpl<>(List.of());

        when(eventService.findPage(any(), eq("status"), eq("진행중"))).thenReturn(fakeEvents);

        mockMvc.perform(get("/api/events")
                        .param("sortBy", "status")
                        .param("status", "진행중"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(eventService).findPage(any(), eq("status"), eq("진행중"));
    }

    @Test
    void getEventDetail_returnsEventDetailResponseAsJson() throws Exception {
        EventDetailResponse detail = new EventDetailResponse(
                2,
                "Opening Week Event",
                "Special benefits for the first week",
                "SCHEDULED",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 7),
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
        );

        when(eventService.findDetail(2)).thenReturn(detail);

        mockMvc.perform(get("/api/events/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.eventId").value(2))
                .andExpect(jsonPath("$.eventTitle").value("Opening Week Event"))
                .andExpect(jsonPath("$.eventDesc").value("Special benefits for the first week"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.eventStartDate").value("2026-08-01"))
                .andExpect(jsonPath("$.eventEndDate").value("2026-08-07"))
                .andExpect(jsonPath("$.concert.concertId").value(101))
                .andExpect(jsonPath("$.concert.concertTitle").value("Summer Festival"))
                .andExpect(jsonPath("$.concert.imgUrl").value("https://cdn.example.com/concerts/101.png"))
                .andExpect(jsonPath("$.concert.concertDesc").value("Three day festival"))
                .andExpect(jsonPath("$.concert.venue").value("Olympic Hall"))
                .andExpect(jsonPath("$.concert.runningTime").value(120))
                .andExpect(jsonPath("$.concert.concertStartDate").value("2026-07-20"))
                .andExpect(jsonPath("$.concert.concertEndDate").value("2026-07-22"))
                .andExpect(jsonPath("$.concert.ageRating").value("15+"));

        verify(eventService).findDetail(2);
    }

    @Test
    void getEventDetail_whenEventMissing_returnsNotFound() throws Exception {
        when(eventService.findDetail(999))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));

        mockMvc.perform(get("/api/events/999"))
                .andExpect(status().isNotFound());

        verify(eventService).findDetail(999);
    }
}
