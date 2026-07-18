package studio.aroudhub.ticketing.domain.concert.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertDetailResponse;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertListResponse;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertRepository;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    @Transactional(readOnly = true)
    // concert 테이블 조회범위: 전체. 조회 컬럼: 일부
    public Page<ConcertListResponse> findPage(Pageable pageable) {
        return concertRepository.findConcertPage(pageable);
    }

    @Transactional(readOnly = true)
    // concert 테이블의 특정 concertId 조회
    public ConcertDetailResponse findDetail(int concertId) {
        Concert concert = concertRepository.findByConcertId(concertId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Concert not found."
                ));
        return ConcertDetailResponse.from(concert);
    }
}
