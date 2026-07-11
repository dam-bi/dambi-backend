package studio.aroudhub.ticketing.domain.event.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import studio.aroudhub.ticketing.domain.event.repository.EventList;
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
    void getEvents_withExistingEvents_returnsEventList() throws Exception {
        // 성공 케이스: 서비스가 이벤트 목록을 반환하면 컨트롤러가 JSON 배열을 200 OK로 응답한다.
        List<EventList> fakeEvents = List.of(
                new EventList(
                        1,
                        101,
                        "Early bird discount",
                        "Discount event for early reservations"
                )
        );

        when(eventService.findAll()).thenReturn(fakeEvents);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].eventId").value(1))
                .andExpect(jsonPath("$[0].concertId").value(101))
                .andExpect(jsonPath("$[0].title").value("Early bird discount"))
                .andExpect(jsonPath("$[0].description").value("Discount event for early reservations"));

        verify(eventService).findAll();
    }

    @Test
    void getEvents_whenServiceThrowsIllegalArgumentException_returnsBadRequest() throws Exception {
        // 실패 케이스: 서비스에서 잘못된 요청 예외가 발생하면 전역 예외 처리기가 400 Bad Request를 반환한다.
        doThrow(new IllegalArgumentException("Event lookup failed."))
                .when(eventService)
                .findAll();

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("Event lookup failed."));

        verify(eventService).findAll();
    }
}
