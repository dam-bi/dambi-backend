package studio.aroudhub.ticketing.domain.concert.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertRepository;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertDetailResponse;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertListResponse;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    @Transactional(readOnly = true)
    public Page<ConcertListResponse> findPage(Pageable pageable, String sortBy, String status) {
        String normalizedSortBy = normalize(sortBy);
        String normalizedStatus = normalize(status);

        if (normalizedSortBy == null || normalizedSortBy.equals("랭킹순")) {
            return concertRepository.findConcertPageOrderByBookingCntDesc(pageable, normalizedStatus);
        }
        if (normalizedSortBy.equals("높은가격순")) {
            return concertRepository.findConcertPageOrderByHighestPriceDesc(pageable, normalizedStatus);
        }
        if (normalizedSortBy.equals("낮은가격순")) {
            return concertRepository.findConcertPageOrderByLowestPriceAsc(pageable, normalizedStatus);
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "지원하지 않는 정렬 방식입니다."
        );
    }

    @Transactional(readOnly = true)
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
