package studio.aroudhub.ticketing.domain.concert.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import studio.aroudhub.ticketing.domain.concert.repository.ConcertDetailResponse;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertListItem;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ShowInfo;
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
                "Midnight Synthwave",
                "Late night electronic showcase",
                "https://cdn.example.com/posters/midnight-synthwave.jpg",
                "Aurora Dome",
                "123 Seoul Street",
                548,
                140,
                LocalDateTime.of(2026, 8, 12, 20, 0),
                LocalDateTime.of(2026, 8, 15, 20, 0),
                "15+",
                132000,
                List.of(
                        new ShowInfo(
                                LocalDateTime.of(2026, 8, 12, 20, 0),
                                "Opening night"
                        )
                )
        );

        when(concertService.findDetail(2)).thenReturn(detail);

        mockMvc.perform(get("/api/concerts/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.concertId").value(2))
                .andExpect(jsonPath("$.title").value("Midnight Synthwave"))
                .andExpect(jsonPath("$.venueName").value("Aurora Dome"))
                .andExpect(jsonPath("$.showList[0].label").value("Opening night"));

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
