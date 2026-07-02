package studio.aroudhub.ticketing.domain.concert.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConcertController.class)
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void concertPage_returnsConcertText() throws Exception {
        mockMvc.perform(get("/concerts")
                        .param("concertId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("concert"));
    }
}