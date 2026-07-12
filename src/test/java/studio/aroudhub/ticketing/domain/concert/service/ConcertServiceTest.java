package studio.aroudhub.ticketing.domain.concert.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.concert.TestEntityFactory;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertListResponse;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertRepository;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertPrice;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertSchedule;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ScheduleShowTime;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

class ConcertServiceTest {

    @Test
    void findPage_returnsRepositoryConcertPage() {
        ConcertRepository concertRepository = mock(ConcertRepository.class);
        ConcertService concertService = new ConcertService(concertRepository);
        PageRequest pageable = PageRequest.of(0, 12);
        Concert concert = mock(Concert.class);
        Venue venue = mock(Venue.class);
        List<ConcertPrice> price = List.of(
                TestEntityFactory.createConcertPrice(1, "VIP", 88000)
        );
        List<ConcertSchedule> date = List.of(
                TestEntityFactory.createConcertSchedule(
                        1,
                        LocalDate.of(2026, 7, 1),
                        List.of(new ScheduleShowTime(1, LocalTime.of(19, 30)))
                )
        );
        Page<Concert> repositoryPage = new PageImpl<>(List.of(concert));

        when(concertRepository.findConcertPage(pageable)).thenReturn(repositoryPage);
        when(concert.getConcertId()).thenReturn(10);
        when(concert.getVenue()).thenReturn(venue);
        when(venue.getVenueId()).thenReturn(4);
        when(concert.getTitle()).thenReturn("River Strings");
        when(concert.getImgUrl()).thenReturn("https://cdn.example.com/river-strings.jpg");
        when(concert.getDescription()).thenReturn("Open air string concert");
        when(concert.getBookingCnt()).thenReturn(201);
        when(concert.getCreatedAt()).thenReturn("2026-06-01");
        when(venue.getName()).thenReturn("Maple Theater");
        when(concert.getRunning_time()).thenReturn(120);
        when(concert.getStartDate()).thenReturn(LocalDate.of(2026, 7, 1));
        when(concert.getEndDate()).thenReturn(LocalDate.of(2026, 7, 10));
        when(concert.getAge_rating()).thenReturn("12+");
        when(concert.getPrice()).thenReturn(price);
        when(concert.getDate()).thenReturn(date);

        Page<ConcertListResponse> result = concertService.findPage(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).concertId()).isEqualTo(10);
        assertThat(result.getContent().get(0).venueId()).isEqualTo(4);
        assertThat(result.getContent().get(0).title()).isEqualTo("River Strings");
        assertThat(result.getContent().get(0).price().get(0).price()).isEqualTo(88000);
        assertThat(result.getContent().get(0).date().get(0).showList().get(0).time()).isEqualTo("19:30");
    }

    @Test
    void findDetail_returnsMappedConcertDetail() {
        ConcertRepository concertRepository = mock(ConcertRepository.class);
        ConcertService concertService = new ConcertService(concertRepository);
        Concert concert = mock(Concert.class);
        Venue venue = mock(Venue.class);
        List<ConcertPrice> price = List.of(
                TestEntityFactory.createConcertPrice(1, "VIP", 121000)
        );
        List<ConcertSchedule> date = List.of(
                TestEntityFactory.createConcertSchedule(
                        1,
                        LocalDate.of(2026, 9, 3),
                        List.of(new ScheduleShowTime(1, LocalTime.of(20, 0)))
                )
        );

        when(concertRepository.findByConcertId(44)).thenReturn(Optional.of(concert));
        when(concert.getConcertId()).thenReturn(44);
        when(concert.getTitle()).thenReturn("Friday Lights");
        when(concert.getDescription()).thenReturn("Arena performance");
        when(concert.getImgUrl()).thenReturn("https://cdn.example.com/friday-lights.jpg");
        when(concert.getVenue()).thenReturn(venue);
        when(venue.getVenueId()).thenReturn(9);
        when(venue.getName()).thenReturn("North Arena");
        when(concert.getBookingCnt()).thenReturn(312);
        when(concert.getCreatedAt()).thenReturn("2026-08-01");
        when(concert.getRunning_time()).thenReturn(150);
        when(concert.getStartDate()).thenReturn(LocalDate.of(2026, 9, 3));
        when(concert.getEndDate()).thenReturn(LocalDate.of(2026, 9, 7));
        when(concert.getAge_rating()).thenReturn("12+");
        when(concert.getPrice()).thenReturn(price);
        when(concert.getDate()).thenReturn(date);

        ConcertListResponse result = concertService.findDetail(44);

        assertThat(result.concertId()).isEqualTo(44);
        assertThat(result.title()).isEqualTo("Friday Lights");
        assertThat(result.venue()).isEqualTo("North Arena");
        assertThat(result.price()).hasSize(1);
        assertThat(result.price().get(0).price()).isEqualTo(121000);
        assertThat(result.date()).hasSize(1);
        assertThat(result.date().get(0).showList().get(0).time()).isEqualTo("20:00");
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
