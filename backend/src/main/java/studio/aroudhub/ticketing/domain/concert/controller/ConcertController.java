package studio.aroudhub.ticketing.domain.concert.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
// @Controller
public class ConcertController {

    @GetMapping("/concerts")
    public String concertPage(@RequestParam Long concertId){
        return "concert"; // 현재는 가비지값 return함. return html 값
    }
}
