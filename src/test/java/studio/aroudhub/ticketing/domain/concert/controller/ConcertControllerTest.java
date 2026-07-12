package studio.aroudhub.ticketing.domain.concert.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import studio.aroudhub.ticketing.domain.concert.TestEntityFactory;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertDetailResponse;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertListItem;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertPrice;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertSchedule;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ScheduleShowTime;
import studio.aroudhub.ticketing.domain.concert.service.ConcertService;
import studio.aroudhub.ticketing.global.exception.GlobalExceptionHandler;

@WebMvcTest(ConcertController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("testWithoutDB")
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConcertService concertService;

    @Test
    void getConcerts_returnsPagedConcertList() throws Exception {
        Page<ConcertListItem> page = new PageImpl<>(List.of(
                new ConcertListItem(
                        1,
                        "Jazz at the River",
                        "https://cdn.example.com/posters/jazz-river.jpg",
                        "Blue Hall",
                        LocalDateTime.of(2026, 7, 20, 19, 0),
                        LocalDateTime.of(2026, 7, 27, 19, 0),
                        99000
                )
        ));

        when(concertService.findPage(any())).thenReturn(page);

        mockMvc.perform(get("/api/concerts")
                        .param("page", "0")
                        .param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].concertId").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Jazz at the River"))
                .andExpect(jsonPath("$.content[0].venueName").value("Blue Hall"))
                .andExpect(jsonPath("$.content[0].price").value(99000));

        verify(concertService).findPage(any());
    }

    @Test
    void getConcert_returnsConcertDetail() throws Exception {
        ConcertDetailResponse detail = new ConcertDetailResponse(
                2,
                7,
                "Midnight Synthwave",
                "https://cdn.example.com/posters/midnight-synthwave.jpg",
                "Late night electronic showcase",
                548,
                "2026-07-01",
                "Aurora Dome",
                140,
                LocalDateTime.of(2026, 8, 12, 20, 0),
                LocalDateTime.of(2026, 8, 15, 20, 0),
                "15+",
                List.of(
                        testConcertPrice(1, "VIP", 132000),
                        testConcertPrice(2, "R", 99000)
                ),
                List.of(
                        testConcertSchedule(
                                1,
                                LocalDate.of(2026, 8, 12),
                                List.of(
                                        new ScheduleShowTime(1, LocalTime.of(20, 0)),
                                        new ScheduleShowTime(2, LocalTime.of(22, 30))
                                )
                        )
                )
        );

        when(concertService.findDetail(2)).thenReturn(detail);

        mockMvc.perform(get("/api/concerts/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.concertId").value(2))
                .andExpect(jsonPath("$.venueId").value(7))
                .andExpect(jsonPath("$.title").value("Midnight Synthwave"))
                .andExpect(jsonPath("$.venue").value("Aurora Dome"))
                .andExpect(jsonPath("$.price[0].rating").value("VIP"))
                .andExpect(jsonPath("$.price[0].price").value(132000))
                .andExpect(jsonPath("$.date[0].date").value("2026-08-12"))
                .andExpect(jsonPath("$.date[0].showList[0].id").value(1))
                .andExpect(jsonPath("$.date[0].showList[0].time").value("20:00:00"));

        verify(concertService).findDetail(2);
    }

    @Test
    void getConcert_whenConcertMissing_returnsNotFound() throws Exception {
        when(concertService.findDetail(999))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Concert not found."
                ));

        mockMvc.perform(get("/api/concerts/999"))
                .andExpect(status().isNotFound());

        verify(concertService).findDetail(999);
    }

    private static ConcertPrice testConcertPrice(int concertPriceId, String rating, int price) {
        return TestEntityFactory.createConcertPrice(concertPriceId, rating, price);
    }

    private static ConcertSchedule testConcertSchedule(
            int concertScheduleId,
            LocalDate date,
            List<ScheduleShowTime> showList
    ) {
        return TestEntityFactory.createConcertSchedule(concertScheduleId, date, showList);
    }
}
