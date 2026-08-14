package studio.aroudhub.ticketing.domain.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.event.repository.DTO.response.ConcertResponse;
import studio.aroudhub.ticketing.domain.event.repository.DTO.response.EventDetailResponse;
import studio.aroudhub.ticketing.domain.event.repository.DTO.response.EventListResponse;
import studio.aroudhub.ticketing.domain.event.repository.EventRepository;

class EventServiceTest {

    @Test
    void findPage_returnsRepositoryPage() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);
        PageRequest pageable = PageRequest.of(0, 10);
        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<EventListResponse> expected = new PageImpl<>(List.of(
                new EventListResponse(
                        7,
                        "Summer package",
                        "https://cdn.example.com/summer.png",
                        "SCHEDULED",
                        LocalDate.of(2026, 7, 5),
                        LocalDate.of(2026, 7, 31)
                )
        ));

        when(eventRepository.findEventPage(expectedPageable, null)).thenReturn(expected);

        Page<EventListResponse> result = eventService.findPage(pageable, null);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void findPage_withoutStatus_usesCreatedAtDescendingSortAndNoStatusFilter() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);
        PageRequest pageable = PageRequest.of(1, 5);
        Pageable expectedPageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<EventListResponse> expected = new PageImpl<>(List.of());

        when(eventRepository.findEventPage(expectedPageable, null)).thenReturn(expected);

        Page<EventListResponse> result = eventService.findPage(pageable, null);

        assertThat(result).isEqualTo(expected);
        verify(eventRepository).findEventPage(expectedPageable, null);
    }

    @Test
    void findPage_withBlankStatus_usesCreatedAtDescendingSortAndNoStatusFilter() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);
        PageRequest pageable = PageRequest.of(0, 10);
        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<EventListResponse> expected = new PageImpl<>(List.of());

        when(eventRepository.findEventPage(expectedPageable, null)).thenReturn(expected);

        Page<EventListResponse> result = eventService.findPage(pageable, "");

        assertThat(result).isEqualTo(expected);
        verify(eventRepository).findEventPage(expectedPageable, null);
    }

    @Test
    void findPage_withStatus_passesStatusFilterToRepository() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);
        PageRequest pageable = PageRequest.of(0, 10);
        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<EventListResponse> expected = new PageImpl<>(List.of());
        String status = "SCHEDULED";

        when(eventRepository.findEventPage(expectedPageable, status)).thenReturn(expected);

        Page<EventListResponse> result = eventService.findPage(pageable, status);

        assertThat(result).isEqualTo(expected);
        verify(eventRepository).findEventPage(expectedPageable, status);
    }

    @Test
    void findPage_withUnsupportedStatus_stillPassesRawStatusToRepository() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);
        PageRequest pageable = PageRequest.of(0, 10);
        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<EventListResponse> expected = new PageImpl<>(List.of());
        String status = "UNKNOWN";

        when(eventRepository.findEventPage(expectedPageable, status)).thenReturn(expected);

        Page<EventListResponse> result = eventService.findPage(pageable, status);

        assertThat(result).isEqualTo(expected);
        verify(eventRepository).findEventPage(expectedPageable, status);
    }

    @Test
    void findPage_whenRepositoryThrows_wrapsAsIllegalArgumentException() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);
        PageRequest pageable = PageRequest.of(0, 10);
        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        when(eventRepository.findEventPage(expectedPageable, null))
                .thenThrow(new RuntimeException("DB failure"));

        assertThatThrownBy(() -> eventService.findPage(pageable, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event lookup failed.");
    }

    @Test
    void findAllWithConcert_returnsRepositoryEventLists() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);
        List<EventListResponse> expected = List.of(
                new EventListResponse(
                        7,
                        "Summer Concert Event",
                        "https://cdn.example.com/summer.png",
                        "SCHEDULED",
                        LocalDate.of(2026, 8, 1),
                        LocalDate.of(2026, 8, 31)
                )
        );

        when(eventRepository.findEventList()).thenReturn(expected);

        List<EventListResponse> result = eventService.findAllWithConcert();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void findDetail_returnsRepositoryEventDetail() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);
        EventDetailResponse expected = new EventDetailResponse(
                44,
                "Summer Opening Event",
                "Special benefits for opening week",
                "SCHEDULED",
                LocalDate.of(2026, 8, 3),
                LocalDate.of(2026, 8, 10),
                new ConcertResponse(
                        8,
                        "Summer Lights",
                        "https://cdn.example.com/summer-lights.png",
                        "Open air night concert",
                        "North Arena",
                        120,
                        LocalDate.of(2026, 8, 12),
                        LocalDate.of(2026, 8, 14),
                        "12+"
                )
        );

        when(eventRepository.findEventDetailByEventId(44)).thenReturn(Optional.of(expected));

        EventDetailResponse result = eventService.findDetail(44);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void findDetail_whenEventMissing_throwsNotFound() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);

        when(eventRepository.findEventDetailByEventId(404)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.findDetail(404))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .hasToString("404 NOT_FOUND");
    }
}
