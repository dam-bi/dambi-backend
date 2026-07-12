package studio.aroudhub.ticketing.domain.concert.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertListResponse;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertRepository;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    @Transactional(readOnly = true)
    public Page<ConcertListResponse> findPage(Pageable pageable) {
        return concertRepository.findConcertPage(pageable)
                .map(ConcertListResponse::from);
    }

    @Transactional(readOnly = true)
    public ConcertListResponse findDetail(int concertId) {
        Concert concert = concertRepository.findByConcertId(concertId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Concert not found."
                ));
        return ConcertListResponse.from(concert);
    }
}
