package studio.aroudhub.ticketing.domain.concert.repository.entity;

import java.time.LocalDateTime;
// 추후 수정 예정.
public record ShowInfo(
        LocalDateTime showTime,
        String label
) {
}
