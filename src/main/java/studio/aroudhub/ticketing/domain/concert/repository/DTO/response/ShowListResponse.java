package studio.aroudhub.ticketing.domain.concert.repository.DTO.response;

import studio.aroudhub.ticketing.domain.concert.repository.entity.ShowList;

import java.time.LocalTime;

public record ShowListResponse(
        int showId,
        LocalTime time
) {
    public static ShowListResponse from(ShowList showList)
    {
        return new ShowListResponse(
                showList.getId(),
                showList.getTime()
        );
    }
}
