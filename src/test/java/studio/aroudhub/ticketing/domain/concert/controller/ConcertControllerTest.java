package studio.aroudhub.ticketing.domain.concert.controller;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertListResponse;
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
        // 콘서트 목록 조회 시 각 항목이 db.json 기준의 전체 콘서트 정보를 담아 반환되는지 확인한다.
        Page<ConcertListResponse> page = new PageImpl<>(List.of(
                new ConcertListResponse(
                        1,
                        3,
                        "Jazz at the River",
                        "https://cdn.example.com/posters/jazz-river.jpg",
                        "Sunset session by the river",
                        87,
                        "2026-06-20",
                        "Blue Hall",
                        150,
                        LocalDate.of(2026, 7, 20),
                        LocalDate.of(2026, 7, 27),
                        "12+",
                        List.of(
                                new ConcertListResponse.PriceItem("VIP", 99000),
                                new ConcertListResponse.PriceItem("R", 77000)
                        ),
                        List.of(
                                new ConcertListResponse.DateItem(
                                        1,
                                        LocalDate.of(2026, 7, 20),
                                        List.of(new ConcertListResponse.ShowListItem(1, "19:00"))
                                )
                        )
                )
        ));

        when(concertService.findPage(any())).thenReturn(page);

        mockMvc.perform(get("/api/concerts")
                        .param("page", "0")
                        .param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].concert_id").value(1))
                .andExpect(jsonPath("$.content[0].venue_id").value(3))
                .andExpect(jsonPath("$.content[0].title").value("Jazz at the River"))
                .andExpect(jsonPath("$.content[0].img_url").value("https://cdn.example.com/posters/jazz-river.jpg"))
                .andExpect(jsonPath("$.content[0].description").value("Sunset session by the river"))
                .andExpect(jsonPath("$.content[0].booking_cnt").value(87))
                .andExpect(jsonPath("$.content[0].created_at").value("2026-06-20"))
                .andExpect(jsonPath("$.content[0].venue").value("Blue Hall"))
                .andExpect(jsonPath("$.content[0].running_time").value(150))
                .andExpect(jsonPath("$.content[0].start_date").value("2026-07-20"))
                .andExpect(jsonPath("$.content[0].end_date").value("2026-07-27"))
                .andExpect(jsonPath("$.content[0].age_rating").value("12+"))
                .andExpect(jsonPath("$.content[0].price[0].price").value(99000))
                .andExpect(jsonPath("$.content[0].date[0].show_list[0].time").value("19:00"));

        verify(concertService).findPage(any());
    }

    @Test
    void getConcert_returnsConcertDetail() throws Exception {
        // 콘서트 상세 조회 시 db.json 샘플 형식의 snake_case JSON이 반환되는지 확인한다.
        ConcertListResponse detail = new ConcertListResponse(
                2,
                7,
                "Midnight Synthwave",
                "https://cdn.example.com/posters/midnight-synthwave.jpg",
                "Late night electronic showcase",
                548,
                "2026-07-01",
                "Aurora Dome",
                140,
                LocalDate.of(2026, 8, 12),
                LocalDate.of(2026, 8, 15),
                "15+",
                List.of(
                        new ConcertListResponse.PriceItem("VIP", 132000),
                        new ConcertListResponse.PriceItem("R", 99000)
                ),
                List.of(
                        new ConcertListResponse.DateItem(
                                1,
                                LocalDate.of(2026, 8, 12),
                                List.of(
                                        new ConcertListResponse.ShowListItem(1, "20:00"),
                                        new ConcertListResponse.ShowListItem(2, "22:30")
                                )
                        )
                )
        );

        when(concertService.findDetail(2)).thenReturn(detail);

        mockMvc.perform(get("/api/concerts/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.concert_id").value(2))
                .andExpect(jsonPath("$.venue_id").value(7))
                .andExpect(jsonPath("$.title").value("Midnight Synthwave"))
                .andExpect(jsonPath("$.img_url").value("https://cdn.example.com/posters/midnight-synthwave.jpg"))
                .andExpect(jsonPath("$.description").value("Late night electronic showcase"))
                .andExpect(jsonPath("$.booking_cnt").value(548))
                .andExpect(jsonPath("$.created_at").value("2026-07-01"))
                .andExpect(jsonPath("$.venue").value("Aurora Dome"))
                .andExpect(jsonPath("$.running_time").value(140))
                .andExpect(jsonPath("$.start_date").value("2026-08-12"))
                .andExpect(jsonPath("$.end_date").value("2026-08-15"))
                .andExpect(jsonPath("$.age_rating").value("15+"))
                .andExpect(jsonPath("$.price[0].rating").value("VIP"))
                .andExpect(jsonPath("$.price[0].price").value(132000))
                .andExpect(jsonPath("$.date[0].date").value("2026-08-12"))
                .andExpect(jsonPath("$.date[0].show_list[0].id").value(1))
                .andExpect(jsonPath("$.date[0].show_list[0].time").value("20:00"));

        verify(concertService).findDetail(2);
    }

    @Test
    void getConcert_whenConcertMissing_returnsNotFound() throws Exception {
        // 존재하지 않는 콘서트 ID로 조회하면 404 응답이 반환되는지 확인한다.
        when(concertService.findDetail(999))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Concert not found."
                ));

        mockMvc.perform(get("/api/concerts/999"))
                .andExpect(status().isNotFound());

        verify(concertService).findDetail(999);
    }
}
