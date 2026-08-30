package studio.aroudhub.ticketing.domain.admin.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PostMapping("/")
    public String admin(){
        return "hello admin page";
    }
}
