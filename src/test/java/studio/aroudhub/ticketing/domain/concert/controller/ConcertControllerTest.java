package studio.aroudhub.ticketing.domain.concert.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertDetailResponse;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertListResponse;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertPriceResponse;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertScheduleResponse;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ShowListResponse;
import studio.aroudhub.ticketing.domain.concert.service.ConcertService;
import studio.aroudhub.ticketing.global.exception.GlobalExceptionHandler;

@WebMvcTest(ConcertController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConcertService concertService;

    @Test
    @DisplayName("콘서트가 존재하면 200 OK와 콘서트 목록을 반환한다")
    void getConcerts_returnsPagedConcertList() throws Exception {
        Page<ConcertListResponse> page = new PageImpl<>(List.of(
                new ConcertListResponse(
                        1,
                        "Jazz at the River",
                        "https://cdn.example.com/posters/jazz-river.jpg",
                        87,
                        "Blue Hall",
                        LocalDate.of(2026, 7, 20),
                        LocalDate.of(2026, 7, 27)
                )
        ));

        when(concertService.findPage(any(), eq(null), eq(null))).thenReturn(page);

        mockMvc.perform(get("/api/concerts")
                        .param("page", "0")
                        .param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].concertId").value(1))
                .andExpect(jsonPath("$.content[0].concertTitle").value("Jazz at the River"))
                .andExpect(jsonPath("$.content[0].imgUrl").value("https://cdn.example.com/posters/jazz-river.jpg"))
                .andExpect(jsonPath("$.content[0].bookingCnt").value(87))
                .andExpect(jsonPath("$.content[0].venue").value("Blue Hall"))
                .andExpect(jsonPath("$.content[0].concertStartDate").value("2026-07-20"))
                .andExpect(jsonPath("$.content[0].concertEndDate").value("2026-07-27"));

        verify(concertService).findPage(any(), eq(null), eq(null));
    }

    @Test
    void getConcerts_withSortBy_passesSortToService() throws Exception {
        Page<ConcertListResponse> page = new PageImpl<>(List.of());

        when(concertService.findPage(any(), eq("높은가격순"), eq(null))).thenReturn(page);

        mockMvc.perform(get("/api/concerts")
                        .param("sortBy", "높은가격순"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(concertService).findPage(any(), eq("높은가격순"), eq(null));
    }

    @Test
    void getConcerts_withStatus_passesStatusToService() throws Exception {
        Page<ConcertListResponse> page = new PageImpl<>(List.of());

        when(concertService.findPage(any(), eq(null), eq("OPEN"))).thenReturn(page);

        mockMvc.perform(get("/api/concerts")
                        .param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(concertService).findPage(any(), eq(null), eq("OPEN"));
    }

    @Test
    void getConcerts_whenSortByUnsupported_returnsNotFound() throws Exception {
        when(concertService.findPage(any(), eq("최신순"), eq(null)))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "지원하지 않는 정렬 방식입니다."
                ));

        mockMvc.perform(get("/api/concerts")
                        .param("sortBy", "최신순"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
                        .hasMessageContaining("지원하지 않는 정렬 방식입니다."));
    }

    @Test
    void getConcert_returnsConcertDetail() throws Exception {
        ConcertDetailResponse detail = new ConcertDetailResponse(
                2,
                "Midnight Synthwave",
                "https://cdn.example.com/posters/midnight-synthwave.jpg",
                "Arena synthwave show",
                548,
                LocalDate.of(2026, 7, 2),
                "Aurora Dome",
                135,
                LocalDate.of(2026, 8, 12),
                LocalDate.of(2026, 8, 15),
                "15+",
                List.of(
                        new ConcertPriceResponse(1, "VIP", 132000),
                        new ConcertPriceResponse(2, "R", 99000)
                ),
                List.of(
                        new ConcertScheduleResponse(
                                1,
                                LocalDate.of(2026, 8, 12),
                                List.of(new ShowListResponse(1, java.time.LocalTime.of(20, 0)))
                        )
                )
        );

        when(concertService.findDetail(2)).thenReturn(detail);

        mockMvc.perform(get("/api/concerts/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.concertId").value(2))
                .andExpect(jsonPath("$.concertTitle").value("Midnight Synthwave"))
                .andExpect(jsonPath("$.imgUrl").value("https://cdn.example.com/posters/midnight-synthwave.jpg"))
                .andExpect(jsonPath("$.bookingCnt").value(548))
                .andExpect(jsonPath("$.venue").value("Aurora Dome"))
                .andExpect(jsonPath("$.concertStartDate").value("2026-08-12"))
                .andExpect(jsonPath("$.concertEndDate").value("2026-08-15"))
                .andExpect(jsonPath("$.ageRating").value("15+"))
                .andExpect(jsonPath("$.seatList[0].rating").value("VIP"))
                .andExpect(jsonPath("$.seatList[0].price").value(132000))
                .andExpect(jsonPath("$.schedule[0].date").value("2026-08-12"))
                .andExpect(jsonPath("$.schedule[0].showList[0].showId").value(1))
                .andExpect(jsonPath("$.schedule[0].showList[0].time").value("20:00:00"));

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
}
