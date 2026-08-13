package studio.aroudhub.ticketing.domain.concert.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.concert.TestEntityFactory;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertRepository;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertDetailResponse;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertListResponse;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertPrice;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertSchedule;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

class ConcertServiceTest {

    @Test
    void findPage_returnsRepositoryConcertPage() {
        ConcertRepository concertRepository = mock(ConcertRepository.class);
        ConcertService concertService = new ConcertService(concertRepository);
        PageRequest pageable = PageRequest.of(0, 12);
        ConcertListResponse concert = new ConcertListResponse(
                10,
                "River Strings",
                "https://cdn.example.com/river-strings.jpg",
                201,
                "Maple Theater",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 10)
        );
        Page<ConcertListResponse> repositoryPage = new PageImpl<>(List.of(concert));

        when(concertRepository.findConcertPageOrderByBookingCntDesc(pageable, null)).thenReturn(repositoryPage);

        Page<ConcertListResponse> result = concertService.findPage(pageable, null, null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).concertId()).isEqualTo(10);
        assertThat(result.getContent().get(0).concertTitle()).isEqualTo("River Strings");
        assertThat(result.getContent().get(0).venue()).isEqualTo("Maple Theater");
        assertThat(result.getContent().get(0).concertStartDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(result.getContent().get(0).concertEndDate()).isEqualTo(LocalDate.of(2026, 7, 10));

        verify(concertRepository).findConcertPageOrderByBookingCntDesc(pageable, null);
    }

    @Test
    void findPage_whenSortByHighestPrice_returnsRepositoryPage() {
        ConcertRepository concertRepository = mock(ConcertRepository.class);
        ConcertService concertService = new ConcertService(concertRepository);
        PageRequest pageable = PageRequest.of(0, 12);
        Page<ConcertListResponse> repositoryPage = new PageImpl<>(List.of());

        when(concertRepository.findConcertPageOrderByHighestPriceDesc(pageable, null)).thenReturn(repositoryPage);

        Page<ConcertListResponse> result = concertService.findPage(pageable, "높은가격순", null);

        assertThat(result).isSameAs(repositoryPage);
        verify(concertRepository).findConcertPageOrderByHighestPriceDesc(pageable, null);
    }

    @Test
    void findPage_whenSortByLowestPrice_returnsRepositoryPage() {
        ConcertRepository concertRepository = mock(ConcertRepository.class);
        ConcertService concertService = new ConcertService(concertRepository);
        PageRequest pageable = PageRequest.of(0, 12);
        Page<ConcertListResponse> repositoryPage = new PageImpl<>(List.of());

        when(concertRepository.findConcertPageOrderByLowestPriceAsc(pageable, null)).thenReturn(repositoryPage);

        Page<ConcertListResponse> result = concertService.findPage(pageable, "낮은가격순", null);

        assertThat(result).isSameAs(repositoryPage);
        verify(concertRepository).findConcertPageOrderByLowestPriceAsc(pageable, null);
    }

    @Test
    void findPage_whenSortByBlank_usesDefaultSort() {
        ConcertRepository concertRepository = mock(ConcertRepository.class);
        ConcertService concertService = new ConcertService(concertRepository);
        PageRequest pageable = PageRequest.of(0, 12);
        Page<ConcertListResponse> repositoryPage = new PageImpl<>(List.of());

        when(concertRepository.findConcertPageOrderByBookingCntDesc(pageable, null)).thenReturn(repositoryPage);

        Page<ConcertListResponse> result = concertService.findPage(pageable, "   ", null);

        assertThat(result).isSameAs(repositoryPage);
        verify(concertRepository).findConcertPageOrderByBookingCntDesc(pageable, null);
    }

    @Test
    void findPage_whenStatusProvided_appliesStatusFilterToRepository() {
        ConcertRepository concertRepository = mock(ConcertRepository.class);
        ConcertService concertService = new ConcertService(concertRepository);
        PageRequest pageable = PageRequest.of(0, 12);
        Page<ConcertListResponse> repositoryPage = new PageImpl<>(List.of());

        when(concertRepository.findConcertPageOrderByBookingCntDesc(pageable, "OPEN")).thenReturn(repositoryPage);

        Page<ConcertListResponse> result = concertService.findPage(pageable, null, " OPEN ");

        assertThat(result).isSameAs(repositoryPage);
        verify(concertRepository).findConcertPageOrderByBookingCntDesc(pageable, "OPEN");
    }

    @Test
    void findPage_whenSortByUnsupported_throwsNotFound() {
        ConcertRepository concertRepository = mock(ConcertRepository.class);
        ConcertService concertService = new ConcertService(concertRepository);
        PageRequest pageable = PageRequest.of(0, 12);

        assertThatThrownBy(() -> concertService.findPage(pageable, "최신순", null))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);
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
                        List.of(TestEntityFactory.createShowList(1, LocalTime.of(20, 0)))
                )
        );

        when(concertRepository.findByConcertId(44)).thenReturn(Optional.of(concert));
        when(concert.getConcertId()).thenReturn(44);
        when(concert.getTitle()).thenReturn("Friday Lights");
        when(concert.getDescription()).thenReturn("Arena performance");
        when(concert.getImgUrl()).thenReturn("https://cdn.example.com/friday-lights.jpg");
        when(concert.getVenue()).thenReturn(venue);
        when(venue.getName()).thenReturn("North Arena");
        when(concert.getBookingCnt()).thenReturn(312);
        when(concert.getStartDate()).thenReturn(LocalDate.of(2026, 9, 3));
        when(concert.getEndDate()).thenReturn(LocalDate.of(2026, 9, 7));
        when(concert.getAgeRating()).thenReturn("12+");
        when(concert.getPrice()).thenReturn(price);
        when(concert.getDate()).thenReturn(date);

        ConcertDetailResponse result = concertService.findDetail(44);

        assertThat(result.concertId()).isEqualTo(44);
        assertThat(result.concertTitle()).isEqualTo("Friday Lights");
        assertThat(result.venue()).isEqualTo("North Arena");
        assertThat(result.seatList()).hasSize(1);
        assertThat(result.seatList().get(0).price()).isEqualTo(121000);
        assertThat(result.schedule()).hasSize(1);
        assertThat(result.schedule().get(0).showList().get(0).time()).isEqualTo(LocalTime.of(20, 0));
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
