package studio.aroudhub.ticketing.domain.event.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import studio.aroudhub.ticketing.domain.event.repository.EventSummary;

@Service
public class EventService {

    public List<EventSummary> findActiveEvents() {
        return List.of(
                new EventSummary(
                        1L,
                        "얼리버드 할인 이벤트",
                        "예매 오픈 초반에만 제공되는 특별 할인 공연입니다.",
                        "할인",
                        LocalDate.of(2026, 5, 20),
                        LocalDate.of(2026, 6, 5),
                        101L,
                        "Summer Night Concert",
                        "https://images.unsplash.com/photo-1501281668745-f7f57925c3b4?auto=format&fit=crop&w=900&q=80"
                ),
                new EventSummary(
                        2L,
                        "신규 회원 웰컴 이벤트",
                        "처음 예매하는 회원에게 추천하는 인기 콘서트입니다.",
                        "회원",
                        LocalDate.of(2026, 5, 25),
                        LocalDate.of(2026, 6, 30),
                        102L,
                        "Acoustic Live Stage",
                        "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=900&q=80"
                ),
                new EventSummary(
                        3L,
                        "주말 공연 추천전",
                        "이번 주말 관람하기 좋은 공연을 모았습니다.",
                        "추천",
                        LocalDate.of(2026, 5, 28),
                        LocalDate.of(2026, 6, 10),
                        103L,
                        "Weekend Jazz Festa",
                        "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?auto=format&fit=crop&w=900&q=80"
                )
        );
    }
}
