package studio.aroudhub.ticketing.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({"/", "/events"})
    public String forward() {
        return "forward:/index.html";
    }
}
