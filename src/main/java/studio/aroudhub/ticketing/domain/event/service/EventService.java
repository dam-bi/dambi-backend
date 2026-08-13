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
import studio.aroudhub.ticketing.domain.event.repository.response.EventDetailResponse;
import studio.aroudhub.ticketing.domain.event.repository.response.EventListResponse;
import studio.aroudhub.ticketing.domain.event.repository.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // eventID 기준 단건 조회.
    public EventDetailResponse findDetail(int eventID){
        return eventRepository.findEventDetailByEventId(eventID)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event 목록을 찾을 수 없습니다."));
    }

    // GET /api/events
    public Page<EventListResponse> findPage(Pageable pageable, String sortBy, String status){

        String normalizedStatus = (status == null || status.isBlank()) ? null : status;

        Sort sort = null; // sort 방식 저장할 변수 sort.
        // if문: sortBy 기준이 없을 시, sort 진행하지 않음.
        if(sortBy == null || sortBy.isBlank()){ sort = Sort.unsorted(); }
        else{
            sort = switch (sortBy){
                case "status" -> Sort.by(Sort.Direction.DESC, "status");
                default -> throw new IllegalArgumentException(sortBy + "은(는) 지원하지 않는 정렬 기준입니다.");
            };
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Objects.requireNonNull(sort));
        return eventRepository.findEventPage(sortedPageable, normalizedStatus);
    }

    //   SELECT * FROM event JOIN ON event.concert_id = concert.concert_id;
    public List<EventListResponse> findAllWithConcert() {
        return eventRepository.findEventList();
    }
}
