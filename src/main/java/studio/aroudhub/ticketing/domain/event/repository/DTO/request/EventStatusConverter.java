package studio.aroudhub.ticketing.domain.event.repository.DTO.request;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EventStatusConverter implements Converter<String, EventStatus> {
    @Override
    public EventStatus convert(String status){
        if( status.equals("전체") ){
            return EventStatus.ALL;
        }
        else if( status.equals("종료") ){
            return EventStatus.END;
        }
        else if( status.equals("진행중") ){
            return EventStatus.ONGOING;
        }
        else if( status.equals("예정") ){
            return EventStatus.SCHEDULED;
        }
        else{
            throw new IllegalArgumentException(
                    status + "은(는) 지원하지 않은 EventStatus 타입입니다."
            );
        }
    }
}
