package studio.aroudhub.ticketing.domain.concert.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertPrice;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertSchedule;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ScheduleShowTime;

public record ConcertListResponse(
        @JsonProperty("concert_id")
        int concertId,
        @JsonProperty("venue_id")
        int venueId,
        String title,
        @JsonProperty("img_url")
        String imgUrl,
        String description,
        @JsonProperty("booking_cnt")
        int bookingCnt,
        @JsonProperty("created_at")
        String createdAt,
        String venue,
        @JsonProperty("running_time")
        int runningTime,
        @JsonProperty("start_date")
        LocalDateTime startDate,
        @JsonProperty("end_date")
        LocalDateTime endDate,
        @JsonProperty("age_rating")
        String ageRating,
        List<PriceItem> price,
        List<DateItem> date
) {
    public static ConcertListResponse from(Concert concert) {
        return new ConcertListResponse(
                concert.getConcertId(),
                concert.getVenue().getVenueId(),
                concert.getTitle(),
                concert.getImgUrl(),
                concert.getDescription(),
                concert.getBookingCnt(),
                concert.getCreatedAt(),
                concert.getVenue().getName(),
                concert.getRunning_time(),
                concert.getStartDate(),
                concert.getEndDate(),
                concert.getAge_rating(),
                concert.getPrice().stream()
                        .map(PriceItem::from)
                        .collect(Collectors.toList()),
                concert.getDate().stream()
                        .map(DateItem::from)
                        .collect(Collectors.toList())
        );
    }

    public record PriceItem(
            String rating,
            int price
    ) {
        static PriceItem from(ConcertPrice concertPrice) {
            return new PriceItem(concertPrice.getRating(), concertPrice.getPrice());
        }
    }

    public record DateItem(
            int id,
            LocalDate date,
            @JsonProperty("show_list")
            List<ShowListItem> showList
    ) {
        static DateItem from(ConcertSchedule concertSchedule) {
            return new DateItem(
                    concertSchedule.getConcertScheduleId(),
                    concertSchedule.getDate(),
                    concertSchedule.getShowList().stream()
                            .map(ShowListItem::from)
                            .collect(Collectors.toList())
            );
        }
    }

    public record ShowListItem(
            int id,
            String time
    ) {
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

        static ShowListItem from(ScheduleShowTime scheduleShowTime) {
            return new ShowListItem(
                    scheduleShowTime.id(),
                    scheduleShowTime.time().format(TIME_FORMATTER)
            );
        }
    }
}
