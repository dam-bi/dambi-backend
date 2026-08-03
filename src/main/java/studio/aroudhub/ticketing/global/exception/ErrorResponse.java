package studio.aroudhub.ticketing.global.exception;

public record ErrorResponse(
        boolean isSuccess,
        String message
) {
}
