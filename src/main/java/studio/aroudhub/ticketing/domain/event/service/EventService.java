package studio.aroudhub.ticketing.domain.event.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.event.repository.DTO.response.EventDetailResponse;
import studio.aroudhub.ticketing.domain.event.repository.DTO.response.EventListResponse;
import studio.aroudhub.ticketing.domain.event.repository.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventDetailResponse findDetail(int eventID) {
        return eventRepository.findEventDetailByEventId(eventID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "이벤트 상세 정보를 찾을 수 없습니다."));
    }

    public Page<EventListResponse> findPage(Pageable pageable, String status) {
        String normalizedStatus = (status == null || status.isBlank()) ? null : status;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Objects.requireNonNull(sort)
        );
        try {
            return eventRepository.findEventPage(sortedPageable, normalizedStatus);
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("이벤트 정보 조회에 실패했습니다.", exception);
        }
    }

    public List<EventListResponse> findAllWithConcert() {
        return eventRepository.findEventList();
    }
}
