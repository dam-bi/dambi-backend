package studio.aroudhub.ticketing.domain.concert.repository.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class ShowList{
    public int id;
    public LocalTime time;

    public ShowList() {
    }

    @JsonCreator
    public ShowList(
            @JsonProperty("id") int id,
            @JsonProperty("time") LocalTime time
    ){
        this.id = id;
        this.time = time;
    }
}
