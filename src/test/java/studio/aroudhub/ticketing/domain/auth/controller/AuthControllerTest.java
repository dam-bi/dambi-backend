package studio.aroudhub.ticketing.domain.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import studio.aroudhub.ticketing.domain.auth.service.AuthService;
import studio.aroudhub.ticketing.global.exception.GlobalExceptionHandler;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void signup_withValidRequest_returnsCreated() throws Exception {
        String requestBody = """
                {
                  "name": "kim",
                  "email": "kim@example.com",
                  "phoneNumber": "010-1234-5678",
                  "password": "secret123"
                }
                """;

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isSuccess").value(true));

        verify(authService).signUp(any());
    }

    @Test
    void signup_withNullEmail_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                  "name": "kim",
                  "email": null,
                  "phoneNumber": "010-1234-5678",
                  "password": "secret123"
                }
                """;

        doThrow(new IllegalArgumentException("Email must not be null."))
                .when(authService)
                .signUp(any());

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("Email must not be null."));

        verify(authService).signUp(any());
    }
}
