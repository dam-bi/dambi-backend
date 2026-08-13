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
    // GET /api/concerts
    // GET /api/concert?sortBy=기준값&status=값
    public Page<ConcertListResponse> findPage(Pageable pageable, String sortBy, String status) {
        String normalizedSortBy = normalize(sortBy);
        normalize(status);

        if (normalizedSortBy == null || normalizedSortBy.equals("랭킹순")) {
            return concertRepository.findConcertPageOrderByBookingCntDesc(pageable);
        }
        if (normalizedSortBy.equals("높은가격순")) {
            return concertRepository.findConcertPageOrderByHighestPriceDesc(pageable);
        }
        if (normalizedSortBy.equals("낮은가격순")) {
            return concertRepository.findConcertPageOrderByLowestPriceAsc(pageable);
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "지원하지 않는 정렬 방식입니다."
        );
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

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
