package studio.aroudhub.ticketing.domain.event.repository.DTO.request;

import lombok.Getter;

@Getter
public enum EventStatus {
    /*
    ALL: 전체
    END: 종료
    ONGOING: 진행중
    SCHEDULED: 예정
     */
    ALL("전체"),
    END("종료"),
    ONGOING("진행중"),
    SCHEDULED("예정");

    private final String label;

    EventStatus(String label) {
        this.label = label;
    }

}
