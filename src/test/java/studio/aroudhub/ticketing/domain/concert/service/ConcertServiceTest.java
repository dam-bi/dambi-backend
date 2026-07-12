package studio.aroudhub.ticketing.domain.concert.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertDetailResponse;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertListItem;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertRepository;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ShowInfo;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

class ConcertServiceTest {

    @Test
    void findPage_returnsRepositoryConcertPage() {
        ConcertRepository concertRepository = mock(ConcertRepository.class);
        ConcertService concertService = new ConcertService(concertRepository);
        PageRequest pageable = PageRequest.of(0, 12);
        Page<ConcertListItem> expected = new PageImpl<>(List.of(
                new ConcertListItem(
                        10,
                        "River Strings",
                        "https://cdn.example.com/river-strings.jpg",
                        "Maple Theater",
                        LocalDateTime.of(2026, 7, 1, 19, 30),
                        LocalDateTime.of(2026, 7, 10, 19, 30),
                        88000
                )
        ));

        when(concertRepository.findConcertPage(pageable)).thenReturn(expected);

        Page<ConcertListItem> result = concertService.findPage(pageable);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void findDetail_returnsMappedConcertDetail() {
        ConcertRepository concertRepository = mock(ConcertRepository.class);
        ConcertService concertService = new ConcertService(concertRepository);
        Concert concert = mock(Concert.class);
        Venue venue = mock(Venue.class);
        List<ShowInfo> showList = List.of(
                new ShowInfo(LocalDateTime.of(2026, 9, 3, 20, 0), "Friday night")
        );

        when(concertRepository.findByConcertId(44)).thenReturn(Optional.of(concert));
        when(concert.getConcertId()).thenReturn(44);
        when(concert.getTitle()).thenReturn("Friday Lights");
        when(concert.getDescription()).thenReturn("Arena performance");
        when(concert.getImgUrl()).thenReturn("https://cdn.example.com/friday-lights.jpg");
        when(concert.getVenue()).thenReturn(venue);
        when(venue.getName()).thenReturn("North Arena");
        when(venue.getAddress()).thenReturn("45 Arena Road");
        when(concert.getBookingCnt()).thenReturn(312);
        when(concert.getRunning_time()).thenReturn(150);
        when(concert.getStartDate()).thenReturn(LocalDateTime.of(2026, 9, 3, 20, 0));
        when(concert.getEndDate()).thenReturn(LocalDateTime.of(2026, 9, 7, 20, 0));
        when(concert.getAge_rating()).thenReturn("12+");
        when(concert.getPrice()).thenReturn(121000);
        when(concert.getShowList()).thenReturn(showList);

        ConcertDetailResponse result = concertService.findDetail(44);

        assertThat(result.concertId()).isEqualTo(44);
        assertThat(result.title()).isEqualTo("Friday Lights");
        assertThat(result.venueName()).isEqualTo("North Arena");
        assertThat(result.showList()).isEqualTo(showList);
    }

    @Test
    void findDetail_whenConcertMissing_throwsNotFound() {
        ConcertRepository concertRepository = mock(ConcertRepository.class);
        ConcertService concertService = new ConcertService(concertRepository);

        when(concertRepository.findByConcertId(404)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> concertService.findDetail(404))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .hasToString("404 NOT_FOUND");
    }
}
