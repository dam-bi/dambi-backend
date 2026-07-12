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
public class ConcertController {

    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    @GetMapping
    public Page<ConcertListItem> getConcerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return concertService.findPage(pageable);
    }

    @GetMapping("/{concertId}")
    public ConcertDetailResponse getConcert(
            @PathVariable int concertId
    ) {
        return concertService.findDetail(concertId);
    }
}
