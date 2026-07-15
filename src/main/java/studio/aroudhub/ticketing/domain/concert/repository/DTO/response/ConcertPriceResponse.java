package studio.aroudhub.ticketing.domain.concert.repository.DTO.response;

import jakarta.persistence.*;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertPrice;

public record ConcertPriceResponse(
        int concertPriceId,
        String rating,
        int price
) {
    public static ConcertPriceResponse from(ConcertPrice concertPrice) {
        return new ConcertPriceResponse(
                concertPrice.getConcertPriceId(),
                concertPrice.getRating(),
                concertPrice.getPrice()
        );
    }
}
