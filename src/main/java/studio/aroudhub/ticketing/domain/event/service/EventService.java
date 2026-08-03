package studio.aroudhub.ticketing.domain.event.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.event.repository.EventDetailResponse;
import studio.aroudhub.ticketing.domain.event.repository.EventListResponse;
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

    // 페이징
    public Page<EventListResponse> findPage(Pageable pageable){
        return eventRepository.findEventPage(pageable);
    }

    //   SELECT * FROM event JOIN ON event.concert_id = concert.concert_id;
    public List<EventListResponse> findAllWithConcert() {
        return eventRepository.findEventList();
    }
}
