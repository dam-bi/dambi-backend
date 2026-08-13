package studio.aroudhub.ticketing.domain.event.repository.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EventStatusTest {

    @Test
    void getLabel_returnsExpectedKoreanLabelForEachStatus() {
        assertThat(EventStatus.ALL.getLabel()).isEqualTo("전체");
        assertThat(EventStatus.END.getLabel()).isEqualTo("종료");
        assertThat(EventStatus.ONGOING.getLabel()).isEqualTo("진행중");
        assertThat(EventStatus.SCHEDULED.getLabel()).isEqualTo("예정");
    }
}
