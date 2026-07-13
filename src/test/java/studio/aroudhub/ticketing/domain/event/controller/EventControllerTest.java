package studio.aroudhub.ticketing.domain.event.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.lang.reflect.Field;
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
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.event.repository.EventWithConcertResponse;
import studio.aroudhub.ticketing.domain.event.service.EventService;
import studio.aroudhub.ticketing.global.exception.GlobalExceptionHandler;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

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
    void getEvents_withExistingEvents_returnsEventWithConcertViewList() throws Exception {
        // 성공 케이스: 서비스가 이벤트 목록을 반환하면 컨트롤러가 JSON 배열을 200 OK로 응답한다.
        Concert concert = createConcert();
        List<EventWithConcertResponse> fakeEvents = List.of(
                new EventWithConcertResponse(
                        1,
                        "Early bird discount",
                        "Discount event for early reservations",
                        "진행중",
                        LocalDate.of(2026, 7, 5),
                        LocalDate.of(2026, 7, 31),
                        concert
                )
        );

        when(eventService.findAllWithConcert()).thenReturn(fakeEvents);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].eventId").value(1))
                .andExpect(jsonPath("$[0].title").value("Early bird discount"))
                .andExpect(jsonPath("$[0].description").value("Discount event for early reservations"))
                .andExpect(jsonPath("$[0].status").value("진행중"))
                .andExpect(jsonPath("$[0].startDate").value("2026-07-05"))
                .andExpect(jsonPath("$[0].endDate").value("2026-07-31"))
                .andExpect(jsonPath("$[0].concert.concertId").value(101))
                .andExpect(jsonPath("$[0].concert.title").value("Summer Festival"))
                .andExpect(jsonPath("$[0].concert.imgUrl").value("https://cdn.example.com/concerts/101.png"))
                .andExpect(jsonPath("$[0].concert.startDate").value("2026-07-20"))
                .andExpect(jsonPath("$[0].concert.endDate").value("2026-07-22"))
                .andExpect(jsonPath("$[0].concert.venue.name").value("Olympic Hall"));

        verify(eventService).findAllWithConcert();
    }

    @Test
    void getEvents_whenServiceThrowsIllegalArgumentException_returnsBadRequest() throws Exception {
        // 실패 케이스: 서비스에서 잘못된 요청 예외가 발생하면 전역 예외 처리기가 400 Bad Request를 반환한다.
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

    private Concert createConcert() throws Exception {
        Concert concert = instantiate(Concert.class);
        Venue venue = instantiate(Venue.class);

        setField(venue, "venueId", 12);
        setField(venue, "name", "Olympic Hall");
        setField(venue, "address", "Seoul");

        setField(concert, "concertId", 101);
        setField(concert, "venue", venue);
        setField(concert, "title", "Summer Festival");
        setField(concert, "imgUrl", "https://cdn.example.com/concerts/101.png");
        setField(concert, "startDate", LocalDate.of(2026, 7, 20));
        setField(concert, "endDate", LocalDate.of(2026, 7, 22));

        return concert;
    }

    private <T> T instantiate(Class<T> type) throws Exception {
        Constructor<T> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
