package studio.aroudhub.ticketing.domain.concert.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertDetailResponse;
import studio.aroudhub.ticketing.domain.concert.repository.ConcertListItem;
import studio.aroudhub.ticketing.domain.concert.service.ConcertService;

@RestController
@RequestMapping("/api/concerts")
// 콘서트 목록 조회와 상세 조회 요청을 처리하는 REST 컨트롤러
public class ConcertController {

    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    @GetMapping
    // 페이지 번호와 크기를 받아 콘서트 목록을 페이징 조회한다.
    public Page<ConcertListItem> getConcerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return concertService.findPage(pageable);
    }

    @GetMapping("/{concertId}")
    // 콘서트 ID로 특정 콘서트의 상세 정보를 조회한다.
    public ConcertDetailResponse getConcert(
            @PathVariable int concertId
    ) {
        return concertService.findDetail(concertId);
    }
}
